package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import lombok.Getter;

/**
 * sql模型.
 *
 * @author yinkaifeng
 * @since 2021-07-20 5:43 下午
 */
@Getter
public final class CustomSql implements Model {

    private final String sql;

    private CustomSql(String sql) {
        this.sql = sql;
    }

    /**
     * 获取自定义sql对象.
     *
     * @param sql 标准sql
     * @return sql对象
     */
    public static CustomSql getCustomSql(String sql) {
        return new CustomSql(sql);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.SQL;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return sql;
    }
}
