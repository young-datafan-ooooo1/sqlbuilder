package com.sensesai.sql.function.impl;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.TimeUnitType;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.model.AbstractFunction;
import com.sensesai.sql.model.Function;

/**
 * Clickhouse函数实现.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:40 下午
 */
public class ClickhouseFunction extends AbstractDatabaseFunctionAbstract {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.CLICKHOUSE;
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
        switch (timeUnitType) {
            case YEAR:
                return "addYears(" + model + "," + value + ")";
            case QUARTER:
                return "addQuarters(" + model + "," + value + ")";
            case MONTH:
                return "addMonths(" + model + "," + value + ")";
            case WEEK:
                return "addWeeks(" + model + "," + value + ")";
            case DAY:
                return "addDays(" + model + "," + value + ")";
            case HOUR:
                return "addHours(" + model + "," + value + ")";
            case MINUTE:
                return "addMinutes(" + model + "," + value + ")";
            case SECOND:
                return "addSeconds(" + model + "," + value + ")";
            default:
                throw new SQLBuildException(getDatabaseType().getCode() + "暂不支持" + timeUnitType.getName() + "时间单位");
        }
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
        switch (timeUnitType) {
            case YEAR:
                return "subtractYears(" + model + "," + value + ")";
            case QUARTER:
                return "subtractQuarters(" + model + "," + value + ")";
            case MONTH:
                return "subtractMonths(" + model + "," + value + ")";
            case WEEK:
                return "subtractWeeks(" + model + "," + value + ")";
            case DAY:
                return "subtractDays(" + model + "," + value + ")";
            case HOUR:
                return "subtractHours(" + model + "," + value + ")";
            case MINUTE:
                return "subtractMinutes(" + model + "," + value + ")";
            case SECOND:
                return "subtractSeconds(" + model + "," + value + ")";
            default:
                throw new SQLBuildException(getDatabaseType().getCode() + "暂不支持" + timeUnitType.getName() + "时间单位");
        }
    }


    /**
     * 时间差函数.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String dateDiff(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        String timeUnit = getDatabaseTimeUnit(function, 3, 0);
        String model1 = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        String model2 = function.getParamList().get(2).getDatabaseSql(getDatabaseType());
        return "dateDiff(" + timeUnit + "," + model1 + "," + model2 + ")";
    }
}
