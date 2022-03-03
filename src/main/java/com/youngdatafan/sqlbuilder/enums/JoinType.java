package com.youngdatafan.sqlbuilder.enums;

import lombok.Getter;

/**
 * 表关联类型枚举.
 *
 * @author yinkaifeng
 * @since 2021-07-14 4:48 下午
 */
@Getter
public enum JoinType {
    LEFT("LEFT JOIN", "左关联"),
    RIGHT("RIGHT JOIN", "右关联"),
    INNER("INNER JOIN", "内关联");

    /**
     * 代码.
     */
    private final String code;
    /**
     * 名称.
     */
    private final String name;

    JoinType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static JoinType getEnumByCode(String code) {
        JoinType[] values = JoinType.values();
        for (JoinType val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
