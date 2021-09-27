package com.sensesai.sql.enums;

import lombok.Getter;

/**
 * 数据类型枚举.
 *
 * @author yinkaifeng
 * @since 2021-07-14 6:16 下午
 */
@Getter
public enum DataType {
    STRING("string", "string"),
    INT("int", "integer"),
    INTEGER("integer", "integer"),
    BIGINT("bigint", "bigint"),
    LONG("long", "long"),
    FLOAT("float", "float"),
    DOUBLE("double", "double"),
    NUMBER("number", "number"),
    DATE("date", "date"),
    DATETIME("datetime", "datetime"),
    TIME("time", "time");

    /**
     * 代码.
     */
    private final String code;

    /**
     * 名称.
     */
    private final String name;

    DataType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static DataType getEnumByCode(String code) {
        DataType[] values = DataType.values();
        for (DataType val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
