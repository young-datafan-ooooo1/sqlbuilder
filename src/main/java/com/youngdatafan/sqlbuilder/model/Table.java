package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import com.youngdatafan.sqlbuilder.util.KeywordUtils;
import com.youngdatafan.sqlbuilder.util.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


/**
 * 表定义.
 *
 * @author yinkaifeng
 * @since 2021-07-14 11:25 上午
 */
@Getter
public final class Table implements Model {
    /**
     * 所属数据库.
     */
    private final Schema schema;

    /**
     * 表名.
     */
    private final String tableName;

    /**
     * 表字段.
     */
    private final List<Field> fields = new ArrayList<>();

    /**
     * 别名.
     */
    private final String alias;

    /**
     * 是否原始表.
     */
    private boolean isOriginal = true;

    /**
     * 非原始表对应的sql.
     */
    private String childSql;

    private Table(Schema schema, String tableName, String alias) {
        this.schema = schema;
        this.tableName = tableName;
        this.alias = alias;
    }

    /**
     * 获取原始表对象.
     *
     * @param schema    schema名称
     * @param tableName 表名
     * @param alias     表别名
     * @return 表对象
     */
    public static Table getOriginalTable(Schema schema, String tableName, String alias) {
        return new Table(schema, tableName, alias);
    }

    /**
     * 获取子查询表对象.
     *
     * @param schema   schema名称
     * @param alias    别名
     * @param childSql 子查询sql
     * @return 子查询表对象
     */
    public static Table getCustomTable(Schema schema, String alias, String childSql) {
        Table table = new Table(schema, alias, alias);
        table.isOriginal = false;
        table.childSql = childSql;
        return table;
    }

    /**
     * 添加表字段.
     *
     * @param fieldName 字段名
     * @return 字段对象
     */
    public Field addField(String fieldName) {
        Field field = Field.getField(this, fieldName);
        fields.add(field);
        return field;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.TABLE;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        String tableSql;
        if (isOriginal) {
            if (schema == null || Utils.isEmpty(schema.getSchemaName())) {
                tableSql = KeywordUtils.replaceKeyword(databaseType, tableName);
            } else {
                tableSql = schema.getDatabaseSql(databaseType) + "." + KeywordUtils.replaceKeyword(databaseType, tableName);
            }
        } else {
            if (this.childSql.trim().startsWith("(")) {
                tableSql = this.childSql;
            } else {
                tableSql = "(" + this.childSql + ")";
            }
        }
        if (alias != null && !"".equals(alias.trim())) {
            tableSql = tableSql + " " + alias;
        }
        return tableSql;
    }
}
