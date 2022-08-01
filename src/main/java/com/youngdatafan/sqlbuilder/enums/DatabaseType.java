package com.youngdatafan.sqlbuilder.enums;

import lombok.Getter;

/**
 * 数据库枚举.
 * 和kettle-database-types.xml 保持一致.
 *
 * @author Echo
 * @version 1.0
 * @date 2021/6/7 11:17
 */
@Getter
public enum DatabaseType {

    /**
     * 华为elk.
     */
    //ELK("elk", "华为elk"),

    /**
     * mysql.
     */
    MYSQL("mysql", "mysql"),

    /**
     * postgresql.
     */
    POSTGRESQL("postgresql", "postgresql"),

    /**
     * KDW.
     */
    KDW("kdw", "kdw"),

    /**
     * oracle.
     */
    ORACLE("oracle", "oracle"),
    /**
     * td.
     */
    //TERADATA("teradata", "teradata"),

    /**
     * sqlserver.
     */
    MSSQL("sqlserver", "sqlserver"),

    /**
     * DB2.
     */
    //DB2("db2", "db2"),

    /**
     * spark.
     */
    SPARK("spark", "spark"),

    /**
     * clickhouse.
     */
    CLICKHOUSE("clickhouse", "clickhouse"),

    /**
     * hive.
     */
    HIVE("hive", "hive");

    /**
     * 代码.
     */
    private final String code;

    /**
     * 名称.
     */
    private final String name;

    DatabaseType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static DatabaseType getEnumByCode(String code) {
        DatabaseType[] values = DatabaseType.values();
        for (DatabaseType val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
