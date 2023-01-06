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
        String nameNew = name;
        if (databaseType.isForceIdentifiersToLowercase()) {
            nameNew = nameNew.toLowerCase();
        } else if (databaseType.isForceIdentifiersToUppercase()) {
            nameNew = nameNew.toUpperCase();
        }
        // 如果字段已经包含引号，我们将不再触摸它，只返回相同的字符串
        if (nameNew.contains(getStartQuote(databaseType)) || nameNew.contains(getEndQuote(databaseType))) {
            return nameNew;
        }
        if (databaseType.isQuoteAllFields()) {
            return getStartQuote(databaseType) + nameNew + getEndQuote(databaseType);
        } else {
            return nameNew;
        }
    }

    /**
     * 获取前置包围符.
     * @param databaseType 数据库类型
     * @return 返回前置包围符
     */
    public static String getStartQuote(DatabaseType databaseType) {
        String quote;
        switch (databaseType) {
            case SPARK:
            case CLICKHOUSE:
            case MYSQL:
            case HIVE:
                quote = "`";
                break;
            case ORACLE:
            case POSTGRESQL:
            case KDW:
                quote = "\"";
                break;
            case MSSQL:
                quote = "[";
                break;
            default:
                quote = "";
        }
        return quote;
    }

    /**
     * 获取后置包围符.
     * @param databaseType 数据库类型
     * @return 返回后置包围符
     */
    public static String getEndQuote(DatabaseType databaseType) {
        String quote;
        switch (databaseType) {
            case SPARK:
            case CLICKHOUSE:
            case MYSQL:
            case HIVE:
                quote = "`";
                break;
            case ORACLE:
            case POSTGRESQL:
            case KDW:
                quote = "\"";
                break;
            case MSSQL:
                quote = "]";
                break;
            default:
                quote = "";
        }
        return quote;
    }
}
