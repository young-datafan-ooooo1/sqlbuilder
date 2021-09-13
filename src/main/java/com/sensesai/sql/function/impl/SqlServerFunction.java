package com.sensesai.sql.function.impl;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.DateFormatType;
import com.sensesai.sql.model.AbstractFunction;
import com.sensesai.sql.model.Function;
import com.sensesai.sql.util.PropertyUtils;

/**
 * sqlserver函数实现.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:40 下午
 */
public class SqlServerFunction extends AbstractDatabaseFunctionAbstract {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MSSQL;
    }

    /**
     * 字符串转时间.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String strToDate(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        String model = function.getParamList().get(0).getDatabaseSql(getDatabaseType());
        DateFormatType dateFormatType = getDateFormatType(function, 2, 1);
        String str;
        switch (dateFormatType) {
            case YYYY_MM_DD:
            case YYYYMMDD:
                str = "date";
                break;
            case HH24MISS:
            case HH24_MI_SS:
                str = "time";
                break;
            default:
                str = "datetime";
        }
        return "CONVERT(" + str + "," + model + ","
                + PropertyUtils.getDatabaseDateFormat(getDatabaseType(), dateFormatType) + ")";
    }

    /**
     * 时间加函数.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String addDateTime(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        String timeUnit = getDatabaseTimeUnit(function, 3, 2);
        String model = function.getParamList().get(0).getDatabaseSql(getDatabaseType());
        String value = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        return "DATEADD(" + timeUnit + "," + value + ", " + model + ")";
    }

    /**
     * 时间减函数.
     *
     * @param abstractFunction 函数模型
     * @return 数据库标准sql.
     */
    public String subDateTime(AbstractFunction abstractFunction) {
        Function function = isFunction(abstractFunction);
        String timeUnit = getDatabaseTimeUnit(function, 3, 2);
        String model = function.getParamList().get(0).getDatabaseSql(getDatabaseType());
        String value = function.getParamList().get(1).getDatabaseSql(getDatabaseType());
        return "DATEADD(" + timeUnit + ",-1*" + value + ", " + model + ")";
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
        return "DATEDIFF(" + timeUnit + "," + model1 + "," + model2 + ")";
    }
}
