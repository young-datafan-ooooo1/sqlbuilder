package com.sensesai.sql.util;

import com.alibaba.fastjson.JSONObject;
import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.function.FunctionDefinition;
import com.sensesai.sql.function.FunctionDefinitionList;
import com.sensesai.sql.function.FunctionParamDefinition;
import com.sensesai.sql.function.IDatabaseFunction;
import com.sensesai.sql.model.AbstractFunction;
import com.sensesai.sql.model.Constant;
import com.sensesai.sql.model.CustomSql;
import com.sensesai.sql.model.Function;
import com.sensesai.sql.model.Model;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * 函数工具类.
 *
 * @author yinkaifeng
 * @since 2021-08-27 10:39 上午
 */
public class FunctionUtils {
    private static final String FIXED = "fixed";

    private static final String RANGE = "range";

    /**
     * 将函数转化为对应数据库标准sql.
     *
     * @param databaseType     数据库类型
     * @param abstractFunction 函数模型
     * @return 数据库标准sql
     */
    public static String toDatabaseSql(DatabaseType databaseType, AbstractFunction abstractFunction) {
        //首先从json定义文件中获取函数，如果json中未定义则从代码中获取对应的函数
        String name = abstractFunction.getType().getCode();
        FunctionDefinition functionDefinition = getFunctionDefinition(databaseType, name);
        if (functionDefinition != null) {
            //参数校验
            verifyParam(databaseType, abstractFunction, functionDefinition);
            return getDatabaseSqlByJson(databaseType, abstractFunction, functionDefinition);
        } else {
            return getDatabaseSqlByJava(databaseType, abstractFunction);
        }
    }

    /**
     * 获取通过json配置的数据库函数标准sql.
     *
     * @param databaseType       数据库类型
     * @param abstractFunction   函数模型
     * @param functionDefinition 函数定义对象
     * @return 数据库函数标准sql
     */
    private static String getDatabaseSqlByJson(DatabaseType databaseType,
                                               AbstractFunction abstractFunction,
                                               FunctionDefinition functionDefinition) {
        if (abstractFunction instanceof Function) {
            Function function = (Function) abstractFunction;
            if (FIXED.equalsIgnoreCase(functionDefinition.getParamSizeType())) {
                //日期格式特殊处理
                Function function1 = dateFormatSpecialTreatment(databaseType, function);
                if (function1 != null) {
                    return getFixedFunctionSql(databaseType, functionDefinition.getFunction(), function1.getParamList());
                }
                return getFixedFunctionSql(databaseType, functionDefinition.getFunction(), function.getParamList());
            } else {
                return getRangeFunctionSql(databaseType, function, functionDefinition);
            }
        }
        throw new SQLBuildException("该类函数暂不可通过json配置");
    }

    /**
     * 日期格式特殊处理，因为不同数据库格式不一样所以特殊处理下.
     *
     * @param databaseType 数据库类型
     * @param function     函数模型
     */
    private static Function dateFormatSpecialTreatment(DatabaseType databaseType, Function function) {
        if (FunctionType.DATE_TO_STRING == function.getFunctionType()
                || FunctionType.STRING_TO_DATE == function.getFunctionType()) {
            Model[] models = new Model[2];
            List<Model> paramList = function.getParamList();
            models[0] = paramList.get(0);
            models[1] = CustomSql.getCustomSql("'" + PropertyUtils.getDatabaseDateFormat(databaseType,
                    paramList.get(1).getDatabaseSql(databaseType)) + "'");
            return Function.getFunction(function.getFunctionType(), models);
        }
        return null;
    }

    /**
     * 获取固定参数个数数据库函数标准sql.
     *
     * @param databaseType 数据库类型
     * @param functionStr  包含变量的函数sql
     * @param paramList    参数
     * @return 标准sql
     */
    private static String getFixedFunctionSql(DatabaseType databaseType, String functionStr, List<Model> paramList) {
        String sql = functionStr;
        for (int i = 0, size = paramList.size(); i < size; i++) {
            sql = sql.replaceAll("#" + (i + 1), paramList.get(i).getDatabaseSql(databaseType));
        }
        return sql;
    }

    /**
     * 获取可变参数数据库函数标准sql.
     *
     * @param databaseType       数据库类型
     * @param function           函数
     * @param functionDefinition 函数定义
     * @return 据库函数标准sql
     */
    private static String getRangeFunctionSql(DatabaseType databaseType, Function function,
                                              FunctionDefinition functionDefinition) {
        StringBuilder sql = new StringBuilder();
        sql.append(functionDefinition.getFunction());
        sql.append(functionDefinition.getLeftQuote());
        List<Model> paramList = function.getParamList();
        boolean isDiv = function.getFunctionType() == FunctionType.DIV;
        String str;
        for (int i = 0, size = paramList.size(); i < size; i++) {
            str = paramList.get(i).getDatabaseSql(databaseType);
            if (isDiv && i > 0) {
                sql.append("(case when ").append(str).append("=0 then null")
                        .append(" else ").append(str).append(" end)");
            } else {
                sql.append(str);
            }
            //最后一个参数不需要分隔符号
            if (i != size - 1) {
                sql.append(functionDefinition.getParamSplit());
            }
        }
        sql.append(functionDefinition.getRightQuote());
        return sql.toString();
    }

    /**
     * 参数校验.
     *
     * @param databaseType       数据库类型
     * @param abstractFunction   函数模型
     * @param functionDefinition 函数定义
     */
    private static void verifyParam(DatabaseType databaseType, AbstractFunction abstractFunction,
                                    FunctionDefinition functionDefinition) {
        if (abstractFunction instanceof Function) {
            Function function = (Function) abstractFunction;
            List<Model> paramList = function.getParamList();
            String paramSizeType = functionDefinition.getParamSizeType();
            int paramMaxSize = functionDefinition.getParamMaxSize();
            int paramMinSize = functionDefinition.getParamMinSize();
            if (FIXED.equalsIgnoreCase(paramSizeType)) {
                if (paramMinSize != function.getParamList().size()) {
                    throw new SQLBuildException("函数" + function.getFunctionType().getCode()
                            + "参数个数不正确，需要：" + paramMinSize + "，实际：" + paramList.size());
                }
            } else if (RANGE.equalsIgnoreCase(paramSizeType)) {
                if (paramList.size() < paramMinSize || paramList.size() > paramMaxSize) {
                    throw new SQLBuildException("函数" + function.getFunctionType().getCode()
                            + "参数个数不正确，最少：" + paramMinSize + "，最多" + paramMaxSize
                            + "，实际：" + paramList.size());
                }
            }
            String checkMsgPrefix = "函数" + function.getFunctionType().getCode();
            isMatch(databaseType, checkMsgPrefix, paramList, functionDefinition.getParams());
        }
    }


    /**
     * 校验函数模型参数是否和函数定义给定的正则表达式匹配.
     * 校验参数是否有意义？
     * 对于函数大部分支持传字段、常量、嵌套函数、标准sql，除了常量其他校验没啥意义。
     *
     * @param databaseType        数据库类型
     * @param checkMsgPrefix      参数校验提示前缀
     * @param paramList           函数模型参数
     * @param paramDefinitionList 函数定义参数列表
     */
    private static void isMatch(DatabaseType databaseType, String checkMsgPrefix,
                                List<Model> paramList, List<FunctionParamDefinition> paramDefinitionList) {
        if (paramList == null || paramList.size() == 0
                || paramDefinitionList == null || paramDefinitionList.size() == 0) {
            return;
        }
        int realSize = paramList.size();
        int needSize = paramDefinitionList.size();
        Model model;
        FunctionParamDefinition functionParamDefinition;
        String checkRegexType;
        String checkMsg = "";
        for (int i = 0; i < realSize; i++) {
            model = paramList.get(i);
            //只有常量的时候才有意义
            if (!(model instanceof Constant)) {
                continue;
            }
            if (needSize > i) {
                functionParamDefinition = paramDefinitionList.get(i);
            } else {
                functionParamDefinition = paramDefinitionList.get(needSize - 1);
            }
            checkRegexType = functionParamDefinition.getCheckRegexType();
            checkMsg = functionParamDefinition.getCheckMsg();
            if (checkRegexType != null && !"".equals(checkRegexType.trim())) {
                if (!isMatchRegex(checkRegexType, ((Constant) model).getOriginalSql(databaseType))) {
                    throw new SQLBuildException(checkMsgPrefix + "第" + (i + 1) + "个参数不正确，错误原因：" + checkMsg == null ? "" : checkMsg);
                }
            }
        }
    }

    /**
     * 判断字符串是否匹配.
     *
     * @param regexTypes 匹配类型
     * @param str      字符串
     * @return 是否匹配
     */
    private static boolean isMatchRegex(String regexTypes, String str) {
        String[] regexTypeArr = regexTypes.split(";");
        boolean sign = true;
        for (String regexType : regexTypeArr) {
            switch (regexType.toLowerCase()) {
                case "int":
                    sign = RegexUtils.isMatchInt(str);
                    break;
                case "number":
                    sign = RegexUtils.isMatchNumber(str);
                    break;
                default:
                    sign = true;
            }
            if (sign) {
                break;
            }
        }
        return sign;
    }

    /**
     * 通过代码实现获取数据库函数sql.
     *
     * @param databaseType 数据库类型
     * @param function     函数模型
     * @return 数据库函数sql
     */
    private static String getDatabaseSqlByJava(DatabaseType databaseType, AbstractFunction function) {
        IDatabaseFunction javaFunction = getDatabaseFunction(databaseType);
        try {
            String methodName = getInvokeMethodName(function.getType());
            Method method = javaFunction.getClass().getMethod(methodName, AbstractFunction.class);
            Object invoke = method.invoke(javaFunction, function);
            return (String) invoke;
        } catch (NoSuchMethodException e) {
            throw new SQLBuildException(databaseType.getCode() + "数据库函数" + function.getType().getCode() + "暂未开发", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof SQLBuildException) {
                throw (SQLBuildException) cause;
            }
            throw new SQLBuildException(databaseType.getCode() + "数据库函数" + function.getType().getCode() + "调用失败", e);
        }
    }

    /**
     * 根据方法类型获取反射调用的方法名称.
     *
     * @param functionType 方法类型
     * @return 方法名称
     */
    private static String getInvokeMethodName(FunctionType functionType) {
        switch (functionType) {
            case STRING_TO_DATE:
                return "strToDate";
            case DATE_TO_STRING:
                return "dateFormat";
            case DATE_DIFF:
                return "dateDiff";
            default:
                return functionType.getCode();
        }
    }


    /**
     * 根据函数名获取对应数据库的函数定义.
     *
     * @param databaseType 数据库类型
     * @param name         函数名
     * @return json函数定义
     */
    private static FunctionDefinition getFunctionDefinition(DatabaseType databaseType, String name) {
        String key = databaseType.getCode() + "_functionDefinition";
        Holder holder = Holder.getOrCreateHolder(key);
        Map<String, FunctionDefinition> instance = (Map<String, FunctionDefinition>) holder.get();
        if (Objects.isNull(instance)) {
            synchronized (holder) {
                if (Objects.isNull(instance)) {
                    instance = loadFunction(databaseType);
                    holder.set(instance);
                }
            }
        }
        return instance.get(name);
    }

    /**
     * 根据数据库类型解析函数定义json文件得到函数定义.
     *
     * @param databaseType 数据库类型枚举
     */
    private static Map<String, FunctionDefinition> loadFunction(DatabaseType databaseType) {
        Map<String, FunctionDefinition> functionDefinitionMap = new HashMap<>();
        String filename = databaseType.getCode().toLowerCase() + "_function.json";
        InputStream is = null;
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            is = resource.getInputStream();
            String jsonStr = IOUtils.toString(is, "utf-8");
            FunctionDefinitionList functionDefinitionList = JSONObject.parseObject(jsonStr, FunctionDefinitionList.class);
            if (functionDefinitionList.getFunctions() != null && functionDefinitionList.getFunctions().size() > 0) {
                for (FunctionDefinition functionDefinition : functionDefinitionList.getFunctions()) {
                    functionDefinitionMap.put(functionDefinition.getName(), functionDefinition);
                }
            }
            return functionDefinitionMap;
        } catch (Exception e) {
            throw new SQLBuildException("加载函数定义配置文件失败", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 获取数据库函数实现类.
     *
     * @param databaseType 数据库类型
     * @return 数据库函数实现类
     */
    private static IDatabaseFunction getDatabaseFunction(DatabaseType databaseType) {
        String key = "databaseFunction";
        Holder holder = Holder.getOrCreateHolder(key);
        Map<DatabaseType, IDatabaseFunction> instance = (Map<DatabaseType, IDatabaseFunction>) holder.get();
        if (Objects.isNull(instance)) {
            synchronized (holder) {
                if (Objects.isNull(instance)) {
                    ServiceLoader<IDatabaseFunction> serviceLoader = ServiceLoader.load(IDatabaseFunction.class);
                    Iterator<IDatabaseFunction> it = serviceLoader.iterator();
                    IDatabaseFunction next;
                    instance = new HashMap<>();
                    while (it.hasNext()) {
                        next = it.next();
                        instance.putIfAbsent(next.getDatabaseType(), next);
                    }
                    holder.set(instance);
                }
            }
        }
        return instance.get(databaseType);
    }
}
