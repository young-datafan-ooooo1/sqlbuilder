package com.sensesai.sql.parser;

import com.google.common.collect.ImmutableList;
import com.sensesai.sql.dto.SqlField;
import com.sensesai.sql.enums.DateFormatType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.enums.TimeUnitType;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.function.FunctionCheckDefinition;
import com.sensesai.sql.model.BinaryCondition;
import com.sensesai.sql.model.Case;
import com.sensesai.sql.model.CustomSql;
import com.sensesai.sql.model.Field;
import com.sensesai.sql.model.Function;
import com.sensesai.sql.model.InCondition;
import com.sensesai.sql.model.Model;
import com.sensesai.sql.model.MultipleModel;
import com.sensesai.sql.model.Table;
import com.sensesai.sql.util.AssertUtils;
import com.sensesai.sql.util.EnumUtils;
import com.sensesai.sql.util.FunCheckUtils;
import com.sensesai.sql.util.RegexUtils;
import com.sensesai.sql.util.Utils;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.fun.SqlCase;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.util.NlsString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 函数表达式解析器.
 * 1、提供解析成Model模型能力。
 * 2、提供获取表达式引用字段能力。
 * 3、提供验证表达式正确性能力。
 * 对于第3个，需要解决的问题，
 *
 * @author yinkaifeng
 * @since 2021-09-08 5:18 下午
 */
public class ExpressionParser {
    /**
     * 条件判断操作符.
     */
    private static final Set<String> SYMBOL = new HashSet<>(
            Arrays.asList("=", "<>", ">", "<", ">=", "<=", "like", "not like", "is null", "is not null", "in", "not in")
    );

    private static final SqlParser.Config CONFIG = SqlParser.configBuilder()
            .setLex(Lex.MYSQL)
            .build();

    /**
     * 是否校验函数表达式.
     */
    private Boolean isCheck = false;

    /**
     * 是否收集字段信息.
     */
    private Boolean isCountField = false;

    /**
     * 函数表达式中表别名和表的映射关系.
     */
    private Map<String, Table> tableMap = new HashMap<>();

    /**
     * 字段对应的数据类型(key：表别名+字段名，value：字段类型).
     */
    private Map<String, String> fieldTypeMap = new HashMap<>();

    /**
     * 表达式中的字段信息.
     */
    private List<SqlField> sqlFields = new ArrayList<>();

    /**
     * 是否验证表达式.
     *
     * @param isCheck 是否验证表达式
     * @return 当前对象
     */
    public ExpressionParser setIsCheck(Boolean isCheck) {
        this.isCheck = isCheck;
        return this;
    }

    /**
     * 是否统计表达式字段信息.
     *
     * @param isCountField 是否统计表达式字段信息
     * @return 当前对象
     */
    public ExpressionParser setIsCountField(Boolean isCountField) {
        this.isCountField = isCountField;
        return this;
    }

    /**
     * 设置表达式中表别名和表映射关系.
     *
     * @param tableMap 表别名和表映射关系
     * @return 当前对象
     */
    public ExpressionParser setTableMap(Map<String, Table> tableMap) {
        if (tableMap != null && !tableMap.isEmpty()) {
            this.tableMap.putAll(tableMap);
        }
        return this;
    }

    /**
     * 设置表达式中字段类型信息.
     *
     * @param fieldList 表达式中字段类型信息
     * @return 当前对象
     */
    public ExpressionParser setFieldTypeInfo(List<SqlField> fieldList) {
        for (SqlField sqlField : fieldList) {
            this.fieldTypeMap.put(getKey(sqlField.getTableAlias(), sqlField.getFieldName()),
                    sqlField.getFieldType());
        }
        return this;
    }

    /**
     * 获取表达式字段信息.
     *
     * @return 表达式字段信息
     */
    public List<SqlField> getSqlFields() {
        return sqlFields;
    }

    /**
     * 解析函数表达式.
     *
     * @param expression 函数表达式
     * @return model模型
     * @throws SqlParseException 解析异常信息
     */
    public Model parseExpression(String expression) throws SqlParseException {
        AssertUtils.notEmpty(expression, "表达式不能为空");
        if (expression.toLowerCase().indexOf(" as ") > 0) {
            throw new SQLBuildException("函数表达式中不能包含as");
        }
        String sql = "select " + expression + " from dual";
        SqlParser parser = SqlParser.create(sql, CONFIG);
        SqlNode sqlNode = parser.parseQuery();
        SqlSelect sqlSelect = (SqlSelect) sqlNode;
        SqlNodeList selectList = sqlSelect.getSelectList();
        if (selectList.size() > 1) {
            throw new SQLBuildException("暂不支持多个表达式：" + expression);
        }
        return parse(selectList.get(0));
    }


    /**
     * 解析查询列.
     *
     * @param sqlNode 查询列
     * @return sql基础模型
     */
    private Model parse(SqlNode sqlNode) {
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            //获取操作符号
            SqlOperator sqlOperator = sqlBasicCall.getOperator();
            //操作符名称
            String operatorName = sqlOperator.getName();
            //参数
            SqlNode[] operands = sqlBasicCall.getOperands();
            //and 或 or 或 比较 或 空判断则转换成case when
            if (("and".equalsIgnoreCase(operatorName)
                    || "or".equalsIgnoreCase(operatorName)
                    || isComparisonSymbol(operatorName))
                    && sqlNode.toString().toLowerCase().indexOf("case") < 0) {
                return convertComparisonSymbolToCase(operatorName, operands);
            }
        }
        return parseSqlNode(sqlNode);
    }


    /**
     * 解析查询列.
     *
     * @param sqlNode 查询列
     * @return sql基础模型
     */
    private Model parseSqlNode(SqlNode sqlNode) {
        if (sqlNode instanceof SqlIdentifier) {
            return parseSqlIdentifier((SqlIdentifier) sqlNode);
        } else if (sqlNode instanceof SqlBasicCall) {
            return parseSqlBasicCall((SqlBasicCall) sqlNode);
        } else if (sqlNode instanceof SqlCase) {
            return parseSqlCase((SqlCase) sqlNode);
        } else if (sqlNode instanceof SqlLiteral) {
            return parseSqlLiteral((SqlLiteral) sqlNode);
        }
        throw new SQLBuildException("暂不支持该表达式：" + sqlNode.toString());
    }


    /**
     * 解析SqlIdentifier.
     *
     * @param sqlIdentifier 对象
     * @return sql基础模型
     */
    private Model parseSqlIdentifier(SqlIdentifier sqlIdentifier) {
        ImmutableList<String> names = sqlIdentifier.names;
        //表别名
        String tableAlias = "";
        //字段名
        String fieldName = "";
        int size = names.size();
        if (size == 2) {
            tableAlias = names.get(0);
            fieldName = names.get(1);
        } else {
            fieldName = names.get(0);
        }
        //验证字段是否存在
        String key = getKey(tableAlias, fieldName);
        if (isCheck && !fieldTypeMap.containsKey(key)) {
            throw new SQLBuildException("字段" + key + "不存在");
        }
        //统计字段信息
        if (isCountField) {
            sqlFields.add(new SqlField(tableAlias, fieldName));
        }
        Table table = getTableByAlias(tableAlias);
        if (table == null) {
            throw new SQLBuildException("字段" + fieldName + "未关联表");
        }
        return Field.getField(table, fieldName);
    }

    /**
     * 解析sqlBasicCall.
     * 这个对象包含了类似 t.salary as b、t.salary >10、abs(t.salary)等
     *
     * @param sqlBasicCall 对象
     * @return sql基础模型
     */
    private Model parseSqlBasicCall(SqlBasicCall sqlBasicCall) {
        //暂时不考虑t.salary as b这种情况，在页面表达式中不太会出现这种情况。
        //获取操作符号
        SqlOperator sqlOperator = sqlBasicCall.getOperator();
        //操作符名称
        String operatorName = sqlOperator.getName();
        //参数
        SqlNode[] operands = sqlBasicCall.getOperands();
        //and 或 or 或 比较 或 空判断则转换成case when
        if ("and".equalsIgnoreCase(operatorName)
                || "or".equalsIgnoreCase(operatorName)
                || isComparisonSymbol(operatorName)) {
            return convertComparisonSymbolToCondition(operatorName, operands);
        }
        //验证函数表达式参数
        if (isCheck) {
            checkFunctionParam(operatorName, operands);
        }
        FunctionType functionType = EnumUtils.getFunctionTypeByCode(operatorName);
        //trim函数有点特殊，这里单独处理
        if ("trim".equalsIgnoreCase(functionType.getCode())) {
            Model[] models = new Model[1];
            models[0] = parseSqlNode(operands[2]);
            return Function.getFunction(functionType, models);
        }
        //函数参数
        Model[] models;
        int len;
        if (Objects.nonNull(operands) && (len = operands.length) > 0) {
            models = new Model[len];
            for (int i = 0; i < len; i++) {
                models[i] = parseSqlNode(operands[i]);
            }
        } else {
            models = new Model[0];
        }
        //count(distinct salary)特殊处理下
        if ("count".equalsIgnoreCase(operatorName)) {
            SqlLiteral functionQuantifier = sqlBasicCall.getFunctionQuantifier();
            if (functionQuantifier != null && "distinct".equalsIgnoreCase(functionQuantifier.getValue().toString())) {
                return Function.getFunction(FunctionType.COUNT_DISTINCT, models[0]);
            }
        }
        return Function.getFunction(functionType, models);
    }

    /**
     * 解析sqlCase对象.
     *
     * @param sqlCase sqlCase对象
     * @return sql基础模型
     */
    private Model parseSqlCase(SqlCase sqlCase) {
        SqlNodeList whenList = sqlCase.getWhenOperands();
        SqlNodeList thenList = sqlCase.getThenOperands();
        SqlNode elseNode = sqlCase.getElseOperand();
        if (Objects.isNull(whenList) || Objects.isNull(thenList) || whenList.size() != thenList.size()) {
            throw new SQLBuildException(sqlCase + "表达式不正确");
        }
        Case caseModel = new Case();
        for (int i = 0, size = whenList.size(); i < size; i++) {
            caseModel.addWhenThen(parseSqlNode(whenList.get(i)),
                    parseSqlNode(thenList.get(i)));
        }
        caseModel.addElse(parseSqlNode(elseNode));
        return caseModel;
    }

    /**
     * 将查询列为布尔值的转换为condition对象.
     *
     * @param operatorName 操作符号
     * @param operands     参数
     * @return sql基础模型
     */
    private Model convertComparisonSymbolToCondition(String operatorName, SqlNode[] operands) {
        if ("and".equalsIgnoreCase(operatorName)
                || "or".equalsIgnoreCase(operatorName)) {
            Model left = parseSqlNode(operands[0]);
            Model right = parseSqlNode(operands[1]);
            return MultipleModel.getMultipleModel(CustomSql.getCustomSql("("), left,
                    CustomSql.getCustomSql(operatorName), right, CustomSql.getCustomSql(")"));
        }
        int size = operands.length;
        if ("is null".equalsIgnoreCase(operatorName) || "is not null".equalsIgnoreCase(operatorName)) {
            if (size != 1) {
                throw new SQLBuildException("is null和is not null前面只能有一个表达式");
            }
            Model left = parseSqlNode(operands[0]);
            if ("is null".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.isNull(left);
            } else {
                return BinaryCondition.isNotNull(left);
            }
        }
        if ("in".equalsIgnoreCase(operatorName) || "not in".equalsIgnoreCase(operatorName)) {
            Model left = parseSqlNode(operands[0]);
            SqlNodeList sqlNodeLists = (SqlNodeList) operands[1];
            int count = sqlNodeLists.size();
            Model[] models = new Model[count];
            for (int i = 0; i < count; i++) {
                models[i] = parseSqlNode(sqlNodeLists.get(i));
            }
            if ("in".equalsIgnoreCase(operatorName)) {
                return InCondition.in(left, models);
            } else {
                return InCondition.notIn(left, models);
            }
        } else if (size == 2) {
            Model left = parseSqlNode(operands[0]);
            Model right = parseSqlNode(operands[1]);
            if ("=".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.equal(left, right);
            } else if ("<>".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.notEqual(left, right);
            } else if (">".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.greaterThan(left, right);
            } else if ("<".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.lessThan(left, right);
            } else if (">=".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.greaterThanOrEqual(left, right);
            } else if ("<=".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.lessThanOrEqual(left, right);
            } else if ("like".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.like(left, right);
            } else if ("not like".equalsIgnoreCase(operatorName)) {
                return BinaryCondition.notLike(left, right);
            }
        }
        throw new SQLBuildException(operatorName + "暂不支持");
    }

    /**
     * 将查询列为布尔值的转换为case when 否则在某些数据库中不能执行.
     *
     * @param operatorName 操作符号
     * @param operands     参数
     * @return sql基础模型
     */
    private Model convertComparisonSymbolToCase(String operatorName, SqlNode[] operands) {
        Case caseModel = new Case();
        Model model = convertComparisonSymbolToCondition(operatorName, operands);
        caseModel.addWhenThen(model, CustomSql.getCustomSql("1"));
        caseModel.addElse(CustomSql.getCustomSql("0"));
        return caseModel;
    }

    /**
     * 解析常量.
     *
     * @param sqlLiteral 对象
     * @return sql基础模型
     */
    private Model parseSqlLiteral(SqlLiteral sqlLiteral) {
        String value = getStringOfSqlLiteral(sqlLiteral);
        return CustomSql.getCustomSql(value);
    }

    /**
     * 判断是否比较符号.
     *
     * @param operatorName 操作符号
     * @return 是否比较符号
     */
    private boolean isComparisonSymbol(String operatorName) {
        return SYMBOL.contains(operatorName.toLowerCase());
    }

    /**
     * 根据表别名获取表模型.
     *
     * @param tableAlias 表别名
     * @return
     */
    private Table getTableByAlias(String tableAlias) {
        Table table = tableMap.get(tableAlias);
        if (table == null) {
            table = tableMap.get("");
        }
        return table;
    }

    /**
     * 校验函数参数.
     * 怎么校验，这是个难题？
     * 难点在于，不同函数参数个数不同，且参数类型也不一致
     *
     * @param operatorName 操作名称
     * @param params       参数列表
     */
    private void checkFunctionParam(String operatorName, SqlNode[] params) {
        FunctionCheckDefinition checkDefinition = FunCheckUtils.getCheckDefinitionByName(operatorName);
        if (Objects.isNull(checkDefinition)) {
            return;
        }
        int paramCount = Objects.isNull(params) ? 0 : params.length;
        if (paramCount < checkDefinition.getMin() || paramCount > checkDefinition.getMax()) {
            if (checkDefinition.getMin().intValue() == checkDefinition.getMax().intValue()) {
                throw new SQLBuildException("函数" + operatorName
                        + "参数个数不正确，需要：" + checkDefinition.getMin() + "，实际：" + paramCount);
            } else {
                throw new SQLBuildException("函数" + operatorName
                        + "参数个数不正确，最少：" + checkDefinition.getMin() + "，最多" + checkDefinition.getMax()
                        + "，实际：" + paramCount);
            }
        }
        if (params != null && params.length > 0) {
            int index = 0;
            String[] array;
            for (SqlNode sqlNode : params) {
                array = FunCheckUtils.getDataType(operatorName, index, params.length);
                if (sqlNode instanceof SqlIdentifier) {
                    checkSqlIdentifier(array, (SqlIdentifier) sqlNode,
                            "函数" + operatorName + "第" + (index + 1) + "个参数");
                } else if (sqlNode instanceof SqlLiteral) {
                    checkSqlLiteral(array, (SqlLiteral) sqlNode,
                            "函数" + operatorName);
                } else if (sqlNode instanceof SqlBasicCall) {
                    checkSqlBasicCall(array, (SqlBasicCall) sqlNode, "");
                }
                index++;
            }
        }
    }

    /**
     * 校验字段是否满足函数要求.
     *
     * @param checkRegexType 函数允许的数据类型
     * @param sqlIdentifier  字段
     * @param prefix         错误信息前缀
     */
    private void checkSqlIdentifier(String[] checkRegexType, SqlIdentifier sqlIdentifier, String prefix) {
        ImmutableList<String> names = sqlIdentifier.names;
        //表别名
        String tableAlias = "";
        //字段名
        String fieldName;
        int size = names.size();
        if (size == 2) {
            tableAlias = names.get(0);
            fieldName = names.get(1);
        } else {
            fieldName = names.get(0);
        }
        String key = getKey(tableAlias, fieldName);
        if (!fieldTypeMap.containsKey(key)) {
            throw new SQLBuildException("字段" + key + "不存在");
        }
        String fieldType = fieldTypeMap.get(key);
        boolean pass = false;
        for (String str : checkRegexType) {
            if ("all".equalsIgnoreCase(str)) {
                pass = true;
                break;
            }
            if (str.equalsIgnoreCase(fieldType) || "string_field".equalsIgnoreCase(fieldType)) {
                pass = true;
                break;
            }
        }
        if (!pass) {
            throw new SQLBuildException(prefix + "，必须是："
                    + convertDataTypeMsg(checkRegexType) + "，实际是：" + fieldType);
        }
    }

    /**
     * 校验常量是否满足函数要求.
     *
     * @param checkRegexType 函数允许的数据类型
     * @param sqlLiteral     常量
     * @param prefix         错误信息前缀
     */
    private void checkSqlLiteral(String[] checkRegexType, SqlLiteral sqlLiteral, String prefix) {
        String value = getStringOfSqlLiteral(sqlLiteral);
        //如果为null则表示满足所有类型
        if ("null".equalsIgnoreCase(value)) {
            return;
        }
        boolean pass = false;
        for (String str : checkRegexType) {
            if ("all".equalsIgnoreCase(str)) {
                pass = true;
                break;
            }

            if (("int".equalsIgnoreCase(str) || "integer".equalsIgnoreCase(str)) && RegexUtils.isMatchInt(value)) {
                pass = true;
                break;
            }
            if ("number".equalsIgnoreCase(str) && RegexUtils.isMatchNumber(value)) {
                pass = true;
                break;
            }
            if ("string".equalsIgnoreCase(str) && RegexUtils.isMatchVarchar(value)) {
                pass = true;
                break;
            }
            if ("date_format".equalsIgnoreCase(str) && RegexUtils.isMatchDateFormat(value)) {
                pass = true;
                break;
            }
            if ("datetime_varchar".equalsIgnoreCase(str) && RegexUtils.isMatchDateTimeVarchar(value)) {
                pass = true;
                break;
            }
            if ("date_varchar".equalsIgnoreCase(str) && RegexUtils.isMatchDateVarchar(value)) {
                pass = true;
                break;
            }
            if ("time_varchar".equalsIgnoreCase(str) && RegexUtils.isMatchTimeVarchar(value)) {
                pass = true;
                break;
            }
            if ("time_unit".equalsIgnoreCase(str) && RegexUtils.isMatchTimeUnit(value)) {
                pass = true;
                break;
            }
        }
        if (!pass) {
            throw new SQLBuildException(prefix + "参数值" + value
                    + "不正确必须是：" + convertDataTypeMsg(checkRegexType));
        }
    }

    /**
     * 校验是否满足函数参数类型.
     *
     * @param checkRegexType 函数允许的数据类型
     * @param sqlBasicCall   sqlBasicCall对象
     * @param prefix         错误信息前缀
     */
    private void checkSqlBasicCall(String[] checkRegexType, SqlBasicCall sqlBasicCall, String prefix) {
        //获取操作符号
        SqlOperator sqlOperator = sqlBasicCall.getOperator();
        //操作符名称
        String operatorName = sqlOperator.getName();
        FunctionCheckDefinition checkDefinition = FunCheckUtils.getCheckDefinitionByName(operatorName);
        if (checkDefinition != null) {
            boolean pass = false;
            for (String str : checkRegexType) {
                if (str.equalsIgnoreCase(checkDefinition.getReturnType())) {
                    pass = true;
                }
            }
            if (!pass) {
                throw new SQLBuildException("表达式" + sqlBasicCall + "返回值类型" + checkDefinition.getReturnType()
                        + "和函数参数所需类型" + convertDataTypeMsg(checkRegexType) + "不匹配");
            }
        }
    }

    private String convertDataTypeMsg(String... array) {
        StringBuilder sb = new StringBuilder();
        int len = array.length;
        for (int i = 0; i < len; i++) {
            sb.append(getChinese(array[i]));
            if (i != len - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String getChinese(String dateType) {
        String s = dateType.toLowerCase();
        switch (s) {
            case "string_field":
                return "文本类型字段";
            case "string":
                return "文本类型";
            case "int":
            case "integer":
                return "整数类型";
            case "time":
                return "时间类型";
            case "date":
                return "日期类型";
            case "datetime":
                return "日期时间类型";
            case "date_format":
                DateFormatType[] values = DateFormatType.values();
                int len = values.length;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    sb.append("'").append(values[i].getCode()).append("'");
                    if (i != len - 1) {
                        sb.append(",");
                    }
                }
                return sb.toString();
            case "time_unit":
                TimeUnitType[] values1 = TimeUnitType.values();
                int len1 = values1.length;
                StringBuilder sb1 = new StringBuilder();
                for (int i = 0; i < len1; i++) {
                    sb1.append("'").append(values1[i].getCode()).append("'");
                    if (i != len1 - 1) {
                        sb1.append(",");
                    }
                }
                return sb1.toString();
            case "datetime_varchar":
                return "日期时间字符串（如：'2021-01-01 08:00:00'、'20210101080000'）";
            case "date_varchar":
                return "日期字符串（如：'2021-01-01'、'20210101'）";
            case "time_varchar":
                return "时间字符串（如：'08:00:00'、'080000'）";
            default:
                return dateType;
        }
    }

    private String getKey(String tableAlias, String fieldName) {
        String key;
        if (Utils.isEmpty(tableAlias)) {
            key = fieldName;
        } else {
            key = tableAlias + "." + fieldName;
        }
        return key;
    }

    private String getStringOfSqlLiteral(SqlLiteral sqlLiteral) {
        Object value = sqlLiteral.getValue();
        if (Objects.isNull(value)) {
            return "null";
        }
        if (value instanceof NlsString) {
            return "'" + ((NlsString) value).getValue() + "'";
        }
        return value.toString();
    }
}
