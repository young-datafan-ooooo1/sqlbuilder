package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.TimeUnitType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.Function;
import com.youngdatafan.sqlbuilder.util.PropertyUtils;

/**
 * oracle函数实现.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:40 下午
 */
public class OracleSqlFunction extends AbstractDatabaseFunctionAbstract {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.ORACLE;
    }

    /**
     * 时间加函数.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String addDateTime(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        TimeUnitType timeUnitType = getTimeUnitType(function, 3, 2);
        String model = function.getParamList().get(0).getDatabaseSql(getDatabaseType());
        String value = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        return addOrSubDateTime(model, value, timeUnitType);
    }

    /**
     * 时间减函数.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String subDateTime(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        TimeUnitType timeUnitType = getTimeUnitType(function, 3, 2);
        String model = function.getParamList().get(0).getDatabaseSql(getDatabaseType());
        String value = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        value = "-1*" + value;
        return addOrSubDateTime(model, value, timeUnitType);
    }


    /**
     * 时间差函数.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String dateDiff(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        TimeUnitType timeUnitType = getTimeUnitType(function, 3, 0);
        String model1 = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        String model2 = function.getParamList().get(2).getDatabaseSql(getDatabaseType());
        switch (timeUnitType) {
            case YEAR:
                return "MONTHS_BETWEEN(" + model1 + "," + model2 + ")/12";
            case QUARTER:
                return "MONTHS_BETWEEN(" + model1 + "," + model2 + ")/3";
            case MONTH:
                return "MONTHS_BETWEEN(" + model1 + "," + model2 + ")";
            case WEEK:
                return "TO_NUMBER(" + model1 + "-" + model2 + ")/7";
            case DAY:
                return "TO_NUMBER(" + model1 + "-" + model2 + ")";
            case HOUR:
                return "TO_NUMBER(" + model1 + "-" + model2 + ")*24";
            case MINUTE:
                return "TO_NUMBER(" + model1 + "-" + model2 + ")*24*60";
            case SECOND:
                return "TO_NUMBER(" + model1 + "-" + model2 + ")*24*60*60";
            default:
                throw new SQLBuildException(getDatabaseType().getCode() + "暂不支持" + timeUnitType.getName() + "时间单位");
        }
    }

    /**
     * 时间加减函数实现.
     *
     * @param modelStr     字段标准sql
     * @param valueStr     值标准sql
     * @param timeUnitType 单位枚举
     * @return 数据库标准sql
     */
    private String addOrSubDateTime(String modelStr, String valueStr, TimeUnitType timeUnitType) {
        switch (timeUnitType) {
            case YEAR:
                return "add_months(" + modelStr + "," + valueStr + "*12)";
            case QUARTER:
                return "add_months(" + modelStr + "," + valueStr + "*3)";
            case MONTH:
                return "add_months(" + modelStr + "," + valueStr + ")";
            case WEEK:
                return "(" + modelStr + "+numtodsinterval(" + valueStr + ",'"
                        + PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), TimeUnitType.DAY) + "'))";
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return "(" + modelStr + "+numtodsinterval(" + valueStr + ",'"
                        + PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), timeUnitType) + "'))";
            default:
                throw new SQLBuildException(getDatabaseType().getCode() + "暂不支持" + timeUnitType.getName() + "时间单位");
        }
    }
}
