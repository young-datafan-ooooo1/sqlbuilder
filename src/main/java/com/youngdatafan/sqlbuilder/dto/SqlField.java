package com.youngdatafan.sqlbuilder.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 字段对象.
 *
 * @author yinkaifeng
 * @since 2021-09-16 9:47 上午
 */
@Getter
@Setter
public class SqlField {
    /**
     * 表别名.
     */
    private String tableAlias;

    /**
     * 字段名.
     */
    private String fieldName;

    /**
     * 字段类型.
     */
    private String fieldType;

    /**
     * 无参构造方法.
     */
    public SqlField() {
    }

    /**
     * 构造方法.
     *
     * @param tableAlias 表别名
     * @param fieldName  字段名
     */
    public SqlField(String tableAlias, String fieldName) {
        this.tableAlias = tableAlias;
        this.fieldName = fieldName;
    }

    /**
     * 构造方法.
     *
     * @param tableAlias 表别名
     * @param fieldName  字段名
     * @param fieldType  字段类型
     */
    public SqlField(String tableAlias, String fieldName, String fieldType) {
        this.tableAlias = tableAlias;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
}
