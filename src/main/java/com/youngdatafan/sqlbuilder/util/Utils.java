package com.youngdatafan.sqlbuilder.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 常用工具类.
 *
 * @author yinkaifeng
 * @since 2021-09-03 2:16 下午
 */
public class Utils {

    /**
     * 判断对象是否为空.
     *
     * @param obj 对象
     * @return 是否为空对象
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return "".equals(((String) obj).trim());
        } else if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * 是否不为空对象.
     *
     * @param obj 对象
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
