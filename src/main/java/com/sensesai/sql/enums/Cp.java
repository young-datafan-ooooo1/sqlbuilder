package com.sensesai.sql.enums;

import lombok.Getter;

/**
 * 组合方式枚举类.
 *
 * @author yinkaifeng
 * @since 2021-09-07 10:39 上午
 */
@Getter
public enum Cp {
    NONE("", "空"),
    AND("AND", "且"),
    OR("OR", "或");

    /**
     * 代码.
     */
    private final String code;

    /**
     * 名称.
     */
    private final String name;

    Cp(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static Cp getEnumByCode(String code) {
        Cp[] values = Cp.values();
        for (Cp val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
