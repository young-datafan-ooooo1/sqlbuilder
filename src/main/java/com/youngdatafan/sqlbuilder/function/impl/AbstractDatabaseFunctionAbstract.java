package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.RankFunction;

/**
 * 数据库函数实现父类，主要实现一些公共的函数.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:43 下午
 */
public abstract class AbstractDatabaseFunctionAbstract extends AbstractJavaFunction {

    /**
     * rank排名函数.
     *
     * @param function 函数模型
     * @return 数据库标准sql
     */
    public String rank(AbstractFunction function) {
        return getRankDatabaseSql("RANK", function);
    }

    /**
     * DENSE_RANK排名函数.
     *
     * @param function 函数模型
     * @return 数据库标准sql
     */
    public String denseRank(AbstractFunction function) {
        return getRankDatabaseSql("DENSE_RANK", function);
    }

    /**
     * ROW_NUMBER排名函数.
     *
     * @param function 函数模型
     * @return 数据库标准sql
     */
    public String rowNumber(AbstractFunction function) {
        return getRankDatabaseSql("ROW_NUMBER", function);
    }


    /**
     * 获取排名函数数据库标准sql.
     *
     * @param keyword  排名函数关键字
     * @param function 函数模型
     * @return 数据库标准sql
     */
    private String getRankDatabaseSql(String keyword, AbstractFunction function) {
        DatabaseType databaseType = getDatabaseType();
        if (databaseType == null) {
            throw new SQLBuildException("数据库类型不能为空");
        }
        if (!(function instanceof RankFunction)) {
            throw new SQLBuildException(function.getModelType() + "不是排名函数");
        }
        RankFunction rankFunction = (RankFunction) function;
        StringBuilder sql = new StringBuilder();
        sql.append(keyword).append(" () OVER (");
        if (rankFunction.getPartitionList() != null && !rankFunction.getPartitionList().isEmpty()) {
            sql.append("PARTITION BY ");
            for (int i = 0, size = rankFunction.getPartitionList().size(); i < size; i++) {
                sql.append(rankFunction.getPartitionList().get(i).getDatabaseSql(databaseType));
                if (i != size - 1) {
                    sql.append(",");
                }
            }
        }
        if (rankFunction.getOrderList() == null || rankFunction.getOrderList().isEmpty()) {
            throw new SQLBuildException("排名函数order by参数不能为空");
        }
        sql.append(" ORDER BY ");
        for (int i = 0, size = rankFunction.getOrderList().size(); i < size; i++) {
            sql.append(rankFunction.getOrderList().get(i).getDatabaseSql(databaseType));
            if (i != size - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return sql.toString();
    }
}
