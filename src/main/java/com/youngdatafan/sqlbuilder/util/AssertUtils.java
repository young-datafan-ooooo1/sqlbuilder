package com.youngdatafan.sqlbuilder.util;

/**
 * 断言工具类.
 *
 * @author yinkaifeng
 * @since 2021-09-03 2:08 下午
 */
public class AssertUtils {

    /**
     * 断言不为null.
     *
     * @param obj     对象
     * @param message 异常信息
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new RuntimeException(message == null ? "对象不能为null" : message);
        }
    }

    /**
     * 断言不为空.
     *
     * @param obj     对象
     * @param message 异常信息
     */
    public static void notEmpty(Object obj, String message) {
        if (Utils.isEmpty(obj)) {
            throw new RuntimeException(message == null ? "对象不能为空" : message);
        }
    }
}
