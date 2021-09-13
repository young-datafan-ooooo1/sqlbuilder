package com.sensesai.sql.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 别名工具类.
 *
 * @author yinkaifeng
 * @since 2021-08-16 6:31 下午
 */
public class AliasUtils {
    public static final ThreadLocal<Map<String, Integer>> CACHE = ThreadLocal.withInitial(HashMap::new);

    /**
     * 获取以prefix为前缀的表别名.
     *
     * @param prefix 前缀
     * @return 别名
     */
    public static String getAlias(String prefix) {
        Map<String, Integer> map = CACHE.get();
        Integer index = map.get(prefix);
        if (Objects.nonNull(index)) {
            map.put(prefix, index + 1);
            CACHE.set(map);
            return prefix + index;
        } else {
            map.put(prefix, 1);
            CACHE.set(map);
            return prefix + 0;
        }
    }

    /**
     * 清空threadlocal中的数据.
     */
    public static void clean() {
        CACHE.remove();
    }
}
