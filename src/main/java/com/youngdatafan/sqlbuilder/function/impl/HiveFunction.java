package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.TimeUnitType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.Function;
import com.youngdatafan.sqlbuilder.util.PropertyUtils;

/**
 * hive函数实现.
 *
 * @author lizihao
 * @since 2022-05-30 10:40 上午
 */
public class HiveFunction extends AbstractDatabaseFunctionAbstract {

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.HIVE;
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
        String unitStr = PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), timeUnitType);
        switch (timeUnitType) {
            case QUARTER:
                return "add_months(" + model + ", 3*" + value + ")";
            case WEEK:
                return "date_add(" + model + ", 7*" + value + ")";
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return "(" + model + "+ INTERVAL " + value + " " + unitStr + ")";
            default:
                throw new SQLBuildException("hive暂不支持该" + timeUnitType.getCode() + "时间单位加减");
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
        String unitStr = PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), timeUnitType);
        switch (timeUnitType) {
            case QUARTER:
                return "add_months(" + model + ", 0-3*" + value + ")";
            case WEEK:
                return "date_add(" + model + ", 0-7*" + value + ")";
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return "(" + model + "- INTERVAL " + value + " " + unitStr + ")";
            default:
                throw new SQLBuildException("hive暂不支持该" + timeUnitType.getCode() + "时间单位加减");
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
        TimeUnitType timeUnitType = getTimeUnitType(function, 3, 0);
        String model1 = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        String model2 = function.getParamList().get(2).getDatabaseSql(getDatabaseType());
        //datediff只支持天的计算，
        switch (timeUnitType) {
            case DAY:
                return "datediff(" + model1 + "," + model2 + ")";
            case QUARTER:
                return "floor(cast(months_between(" + model1 + "," + model2 + ")as int)/3)";
            case YEAR:
                return "cast(datediff(" + model1 + "," + model2 + ")/365.25 as int)";
            case MONTH:
                return "cast(months_between(" + model1 + "," + model2 + ")as int)";
            case WEEK:
                return "cast(datediff(" + model1 + "," + model2 + ")/7 as int)";
            case HOUR:
                return "cast(unix_timestamp(" + model1 + ", 'yyyy-MM-dd HH:mm') - unix_timestamp(" + model2 + ", 'yyyy-MM-dd HH:mm')/3600 as int)";
            case MINUTE:
                return "cast(unix_timestamp(" + model1 + ", 'yyyy-MM-dd HH:mm:ss') - unix_timestamp(" + model2 + ", 'yyyy-MM-dd HH:mm:ss')/60 as int)";
            case SECOND:
                return "cast(unix_timestamp(" + model1 + ", 'yyyy-MM-dd HH:mm:ss') - unix_timestamp(" + model2 + ", 'yyyy-MM-dd HH:mm:ss') as int)";
            default:
                throw new SQLBuildException("hive暂不支持" + timeUnitType.getCode() + "时间单位间隔");
        }
    }
}
