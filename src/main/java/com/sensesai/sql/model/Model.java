package com.sensesai.sql.model;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.ModelType;

/**
 * sql模型接口.
 *
 * @author yinkaifeng
 * @since 2021-08-26 4:08 下午
 */
public interface Model {
    /**
     * 获取模型类型枚举.
     *
     * @return 模型类型枚举
     */
    ModelType getModelType();


    /**
     * 根据数据库类型获取对应数据库的标准sql.
     *
     * @param databaseType 数据库类型
     * @return 数据库的标准sql
     */
    String getDatabaseSql(DatabaseType databaseType);
}
