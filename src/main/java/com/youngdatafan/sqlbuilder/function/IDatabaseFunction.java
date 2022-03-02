package com.youngdatafan.sqlbuilder.function;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;

/**
 * 函数接口.
 *
 * @author yinkaifeng
 * @since 2021-08-27 2:34 下午
 */
public interface IDatabaseFunction {
    /**
     * 获取是那个数据库函数的实现.
     *
     * @return 数据库类型枚举
     */
    DatabaseType getDatabaseType();
}
