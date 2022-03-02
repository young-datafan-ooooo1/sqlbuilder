package com.youngdatafan.sqlbuilder.util;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持有者，这个类主要用于并发时防止重复执行相应代码设计.
 *
 * @author yinkaifeng
 * @since 2021-08-02 3:05 下午
 */
public class Holder<T> {
    private static ConcurrentHashMap<String, Holder> cacheInstances = new ConcurrentHashMap<>();

    private T instance;

    /**
     * 根据名称创建或获取holder对象.
     *
     * @param name 名称
     * @return holder对象
     */
    public static Holder getOrCreateHolder(String name) {
        Holder holder = cacheInstances.get(name);
        if (Objects.isNull(holder)) {
            holder = new Holder();
            cacheInstances.putIfAbsent(name, holder);
        }
        return cacheInstances.get(name);
    }

    /**
     * 获取持有实例.
     *
     * @return 实例
     */
    public T get() {
        return instance;
    }

    /**
     * 设置持有实例.
     *
     * @param instance 实例
     */
    public void set(T instance) {
        this.instance = instance;
    }
}
