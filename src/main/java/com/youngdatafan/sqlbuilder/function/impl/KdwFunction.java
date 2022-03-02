package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.TimeUnitType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.Function;
import com.youngdatafan.sqlbuilder.util.PropertyUtils;

/**
 * kdw函数实现.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:40 下午
 */
public class KdwFunction extends AbstractDatabaseFunctionAbstract {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.KDW;
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
        String str = "DATE_PART('epoch'," + model1 + "-" + model2 + ")";
        switch (timeUnitType) {
            case YEAR:
                return "ROUND(" + str + "/(60*60*24*365))";
            case QUARTER:
                return "ROUND(" + str + "/(60*60*24*90))";
            case MONTH:
                return "ROUND(" + str + "/(60*60*24*30))";
            case WEEK:
                return "ROUND(" + str + "/(60*60*24*7))";
            case DAY:
                return "ROUND(" + str + "/(60*60*24))";
            case HOUR:
                return "ROUND(" + str + "/(60*60))";
            case MINUTE:
                return "ROUND(" + str + "/60)";
            case SECOND:
                return "ROUND(" + str + ")";
            default:
                throw new SQLBuildException(getDatabaseType().getCode() + "暂不支持" + timeUnitType.getName() + "时间单位");
        }
    }

    /**
     * 时间加减函数实现.
     *
     * @param model        字段标准sql
     * @param value        值标准sql
     * @param timeUnitType 单位枚举
     * @return 数据库标准sql
     */
    private String addOrSubDateTime(String model, String value, TimeUnitType timeUnitType) {
        if (timeUnitType == TimeUnitType.QUARTER) {
            return "(" + model + "+INTERVAL '" + value + "*3 "
                    + PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), TimeUnitType.MONTH) + "')";
        }
        return "(" + model + "+INTERVAL '" + value + " "
                + PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), timeUnitType) + "')";
    }
}
