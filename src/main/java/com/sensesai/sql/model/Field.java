package com.sensesai.sql.model;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.ModelType;
import com.sensesai.sql.util.AssertUtils;
import com.sensesai.sql.util.KeywordUtils;
import com.sensesai.sql.util.Utils;
import lombok.Getter;

/**
 * 表字段模型.
 *
 * @author yinkaifeng
 * @since 2021-07-20 9:12 上午
 */
@Getter
public final class Field implements Model {
    /**
     * 所属表.
     */
    private final Table table;

    /**
     * 字段名.
     */
    private final String filedName;

    private Field(Table table, String filedName) {
        AssertUtils.notEmpty(filedName, "字段名不能为空");
        this.table = table;
        this.filedName = filedName;
    }

    /**
     * 获取字段对象.
     *
     * @param table     表对象
     * @param filedName 字段名
     * @return 字段对象
     */
    public static Field getField(Table table, String filedName) {
        return new Field(table, filedName);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.FIELD;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        String sql = KeywordUtils.replaceKeyword(databaseType, filedName);
        if (table != null && Utils.isNotEmpty(table.getAlias())) {
            sql = table.getAlias() + "." + sql;
        }
        return sql;
    }
}
