package com.youngdatafan.sqlbuilder.enums;

import lombok.Getter;

/**
 * 时间单位枚举.
 *
 * @author yinkaifeng
 * @since 2021-07-30 9:25 上午
 */
@Getter
public enum TimeUnitType {
    YEAR("year", "年"),
    QUARTER("quarter", "季"),
    MONTH("month", "月"),
    WEEK("week", "周"),
    DAY("day", "天"),
    HOUR("hour", "小时"),
    MINUTE("minute", "分钟"),
    SECOND("second", "秒");
    /**
     * 代码.
     */
    private final String code;
    /**
     * 名称.
     */
    private final String name;

    TimeUnitType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static TimeUnitType getEnumByCode(String code) {
        TimeUnitType[] values = TimeUnitType.values();
        for (TimeUnitType val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
