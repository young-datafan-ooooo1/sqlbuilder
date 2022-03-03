package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import com.youngdatafan.sqlbuilder.util.Utils;
import lombok.Getter;

/**
 * schema类.
 *
 * @author yinkaifeng
 * @since 2021-07-20 8:28 上午
 */
@Getter
public final class Schema implements Model {
    /**
     * schema名称.
     */
    private final String schemaName;

    private Schema(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * 获取schema对象.
     *
     * @param schemaName schema名称
     * @return schema对象
     */
    public static Schema getSchema(String schemaName) {
        if (Utils.isEmpty(schemaName)) {
            return new Schema("");
        }
        return new Schema(schemaName);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.SCHEMA;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return schemaName;
    }
}
