package com.youngdatafan.sqlbuilder.util;

import com.youngdatafan.sqlbuilder.enums.DataType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;

import java.sql.Types;

/**
 * jdbc工具类.
 *
 * @author yinkaifeng
 * @since 2021-09-27 3:43 下午
 */
public class JdbcUtils {

    /**
     * 将数据库对应的数据类型转换为DataType枚举类型.
     *
     * @param dbDataType 数据库类型
     * @return DataType枚举类型
     */
    public static DataType convertToDataType(int dbDataType) {
        switch (dbDataType) {
            case Types.BIT:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return DataType.INT;
            case Types.BIGINT:
                return DataType.LONG;
            case Types.FLOAT:
                return DataType.FLOAT;
            case Types.DOUBLE:
                return DataType.DOUBLE;
            case Types.NUMERIC:
            case Types.DECIMAL:
                return DataType.NUMBER;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return DataType.STRING;
            case Types.DATE:
                return DataType.DATE;
            case Types.TIME:
                return DataType.TIME;
            case Types.TIMESTAMP:
                return DataType.DATETIME;
            default:
                throw new SQLBuildException("暂不支持该数据类型");
        }
    }
}
