package com.youngdatafan.sqlbuilder.enums;

import lombok.Getter;

/**
 * 条件操作枚举类.
 *
 * @author yinkaifeng
 * @since 2021-09-07 10:40 上午
 */
@Getter
public enum Op {
    /**
     * (" < ").
     */
    LESS_THAN("<", "小于"),

    /**
     * (" <= ").
     */
    LESS_THAN_OR_EQUAL("<=", "小于等于"),

    /**
     * (" > ").
     */
    GREATER_THAN(">", "大于"),

    /**
     * (" >= ").
     */
    GREATER_THAN_OR_EQUAL(">=", "大于等于"),

    /**
     * (" = ").
     */
    EQUAL("=", "等于"),

    /**
     * (" <> ").
     */
    NOT_EQUAL("<>", "不等于"),

    /**
     * (" LIKE ").
     */
    LIKE("LIKE", "匹配"),

    /**
     * (" NOT LIKE ").
     */
    NOT_LIKE("NOT LIKE", "不匹配"),

    /**
     * (" IN ").
     */
    IN("IN", "在"),

    /**
     * (" NOT IN ").
     */
    NOT_IN("NOT IN", "不在"),

    /**
     * (" IS NULL ").
     */
    IS_NULL("IS NULL", "是否为null"),

    /**
     * (" IS NOT NULL ").
     */
    NOT_NULL("IS NOT NULL", "是否不为null"),

    /**
     * ("BETWEEN #1 and #2").
     */
    BETWEEN("BETWEEN", "倆着之间");

    /**
     * 代码.
     */
    private String code;

    /**
     * 名称.
     */
    private String name;

    Op(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static Op getEnumByCode(String code) {
        Op[] values = Op.values();
        for (Op val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
