package com.youngdatafan.sqlbuilder.util;

import com.youngdatafan.sqlbuilder.parser.StellaSqlParser;

/**
 * 表名替换工具类.
 *
 * @author yinkaifeng
 * @since 2021-09-27 11:28 上午
 */
public class TableNameReplaceUtils {

    /**
     * 替换sql中的表名.
     *
     * @param quotingStr   包围符号 `或"或[]
     * @param sql          sql语句
     * @param oldTableName 旧表名
     * @param newTableName 新表名
     * @return 替换后的sql
     */
    public static String replace(String quotingStr, String sql, String oldTableName, String newTableName) {
        StellaSqlParser stellaSqlParser = new StellaSqlParser(quotingStr, oldTableName, newTableName);
        return stellaSqlParser.replace(sql);
    }
}
