package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.TimeUnitType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.Function;
import com.youngdatafan.sqlbuilder.util.PropertyUtils;

/**
 * spark函数实现.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:40 下午
 */
public class SparkFunction extends AbstractDatabaseFunctionAbstract {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SPARK;
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
                return "(" + model + "+ INTERVAL 3*" + value + " " + unitStr + ")";
            case YEAR:
            case MONTH:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return "(" + model + "+ INTERVAL " + value + " " + unitStr + ")";
            default:
                throw new SQLBuildException("spark暂不支持该" + timeUnitType.getCode() + "时间单位加减");
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
                return "(" + model + "- INTERVAL 3*" + value + " " + unitStr + ")";
            case YEAR:
            case MONTH:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return "(" + model + "- INTERVAL " + value + " " + unitStr + ")";
            default:
                throw new SQLBuildException("spark暂不支持该" + timeUnitType.getCode() + "时间单位加减");
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
        String day = "datediff(" + model1 + "," + model2 + ")";
        switch (timeUnitType) {
            case DAY:
                return day;
            default:
                throw new SQLBuildException("spark暂不支持" + timeUnitType.getCode() + "时间单位间隔");
        }
    }
}
