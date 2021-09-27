package com.sensesai.sql.enums;

import lombok.Getter;

/**
 * 时间格式枚举.
 *
 * @author yinkaifeng
 * @since 2021-07-30 9:20 上午
 */
@Getter
public enum DateFormatType {
    YYYY_MM_DD_HH24_MI_SS("yyyy-MM-dd HH24:mi:ss", "yyyy-MM-dd HH24:mi:ss"),
    YYYYMMDDHH24MISS("yyyyMMddHH24miss", "yyyyMMddHH24miss"),
    YYYY_MM_DD("yyyy-MM-dd", "yyyy-MM-dd"),
    YYYYMMDD("yyyyMMdd", "yyyyMMdd"),
    HH24_MI_SS("HH24:mi:ss", "HH24:mi:ss"),
    HH24MISS("HH24miss", "HH24miss");
    /**
     * 代码.
     */
    private final String code;
    /**
     * 名称.
     */
    private final String name;

    DateFormatType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static DateFormatType getEnumByCode(String code) {
        DateFormatType[] values = DateFormatType.values();
        for (DateFormatType val : values) {
            if (val.getCode().equals(code)) {
                return val;
            }
        }
        return null;
    }
}
