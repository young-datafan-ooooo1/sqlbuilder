package com.youngdatafan.sqlbuilder.util;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;

/**
 * 数据库关键字常量.
 *
 * @author yinkaifeng
 * @since 2021-07-20 8:35 上午
 */
public class KeywordUtils {

    /**
     * 针对不同数据库字段和表的转义.
     *
     * @param databaseType 数据库类型
     * @param name         名称
     * @return 转义后的名称
     */
    public static String replaceKeyword(DatabaseType databaseType, String name) {
        if (name.startsWith("`") || name.startsWith("\"")) {
            return name;
        }
        switch (databaseType) {
            case MYSQL:
                return "`" + name + "`";
            case ORACLE:
                return "\"" + name + "\"";
            default:
                return name;
        }
    }
}
