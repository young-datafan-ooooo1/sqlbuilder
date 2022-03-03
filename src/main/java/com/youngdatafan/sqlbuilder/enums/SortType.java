package com.youngdatafan.sqlbuilder.enums;

import lombok.Getter;

/**
 * 排序类型枚举.
 *
 * @author yinkaifeng
 * @since 2021-07-14 4:48 下午
 */
@Getter
public enum SortType {
    ASC("ASC", "升序"),
    DESC("DESC", "降序");
    /**
     * 代码.
     */
    private final String code;
    /**
     * 名称.
     */
    private final String name;

    SortType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static SortType getEnumByCode(String code) {
        SortType[] values = SortType.values();
        for (SortType val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
