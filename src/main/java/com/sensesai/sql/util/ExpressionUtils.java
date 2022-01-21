package com.sensesai.sql.util;

import com.sensesai.sql.dto.SqlField;
import com.sensesai.sql.enums.DataType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.model.CustomSql;
import com.sensesai.sql.model.Function;
import com.sensesai.sql.model.Model;
import com.sensesai.sql.model.Table;
import com.sensesai.sql.parser.ExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 表达式工具类.
 *
 * @author yinkaifeng
 * @since 2021-10-08 10:36 上午
 */
public class ExpressionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionUtils.class);

    /**
     * 将表达式字符串转换成Model对象.
     *
     * @param expressStr 表达式字符串
     * @param tableMap   表达式中别名对应的表信息
     * @return Model对象
     */
    public static Model toModel(String expressStr, Map<String, Table> tableMap) {
        ExpressionParser parser = new ExpressionParser();
        parser.setTableMap(tableMap);
        Model model;
        try {
            model = parser.parseExpression(expressStr);
        } catch (Exception e) {
            LOGGER.error("表达式解析失败", e);
            model = CustomSql.getCustomSql(expressStr);
        }
        return model;
    }

    /**
     * 验证表达式字符串的正确性（不正确会抛出异常).
     *
     * @param expressStr 表达式字符串
     * @param tableMap   表达式中别名对应的表信息
     * @param fieldList  表达式中用到的字段类型信息
     */
    public static void verify(String expressStr, Map<String, Table> tableMap, List<SqlField> fieldList) {
        ExpressionParser parser = new ExpressionParser();
        parser.setTableMap(tableMap)
                .setIsCheck(true)
                .setFieldTypeInfo(fieldList);
        try {
            parser.parseExpression(expressStr);
        } catch (SQLBuildException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("表达式解析失败", e);
            throw new SQLBuildException("表达式验证失败");
        }
    }

    /**
     * 验证表达式字符串的正确性（不正确会抛出异常).
     *
     * @param expressStr 表达式字符串
     * @param tableMap   表达式中别名对应的表信息
     * @param fieldList  表达式中用到的字段类型信息
     * @return DataType 类型
     */
    public static DataType verifyAndGetDataType(String expressStr,
                                                Map<String, Table> tableMap,
                                                List<SqlField> fieldList) {
        ExpressionParser parser = new ExpressionParser();
        parser.setTableMap(tableMap)
                .setIsCheck(true)
                .setFieldTypeInfo(fieldList);
        try {
            Model model = parser.parseExpression(expressStr);
            return getModelDataType(model);
        } catch (SQLBuildException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("表达式解析失败", e);
            throw new SQLBuildException("表达式验证失败");
        }
    }

    /**
     * 获取表达式中的字段信息.
     *
     * @param expressStr 表达式字符串
     * @param tableMap   表达式中别名对应的表信息
     * @return 表达式中的字段信息
     */
    public static List<SqlField> getFields(String expressStr, Map<String, Table> tableMap) {
        ExpressionParser parser = new ExpressionParser();
        parser.setTableMap(tableMap)
                .setIsCountField(true);
        try {
            parser.parseExpression(expressStr);
        } catch (Exception e) {
            LOGGER.error("表达式解析失败", e);
            throw new SQLBuildException("表达式验证失败");
        }
        return parser.getSqlFields();
    }

    private static DataType getModelDataType(Model model) {
        if (model instanceof CustomSql) {
            CustomSql customSql = (CustomSql) model;
            boolean flag = customSql.getSql().startsWith("'");
            if (flag) {
                return DataType.STRING;
            } else if (RegexUtils.isMatchNumber(customSql.getSql())) {
                return DataType.NUMBER;
            }
        } else if (model instanceof Function) {
            Function function = (Function) model;
            FunctionType functionType = function.getFunctionType();
            return getDataTypeByFunctionType(functionType);
        }
        return DataType.STRING;
    }

    private static DataType getDataTypeByFunctionType(FunctionType functionType) {
        switch (functionType) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case POWER:
            case PERCENTAGE:
            case ABS:
            case MOD:
            case ROUND:
            case RAND:
            case DATE_DIFF:
                return DataType.NUMBER;
            case CEIL:
            case FLOOR:
            case LENGTH:
            case INSTR:
                return DataType.INTEGER;
            case STRING_TO_DATE:
            case ADD_DATE_TIME:
            case SUB_DATE_TIME:
            case CURRENT_TIMESTAMP:
                return DataType.TIMESTAMP;
            case YEAR_BEGIN:
            case YEAR_END:
            case QUARTER_BEGIN:
            case QUARTER_END:
            case MONTH_BEGIN:
            case MONTH_END:
            case WEEK_BEGIN:
            case WEEK_END:
                return DataType.DATE;
            default:
                return DataType.STRING;
        }
    }
}
