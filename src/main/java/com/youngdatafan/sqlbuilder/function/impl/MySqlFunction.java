package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.Function;

/**
 * mysql函数实现.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:40 下午
 */
public class MySqlFunction extends AbstractDatabaseFunctionAbstract {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
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
        return "date_add(" + model + ", interval " + value + " " + timeUnit + ")";
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
        return "date_sub(" + model + ", interval " + value + " " + timeUnit + ")";
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
        return "TIMESTAMPDIFF(" + timeUnit + "," + model1 + "," + model2 + ")";
    }
}
