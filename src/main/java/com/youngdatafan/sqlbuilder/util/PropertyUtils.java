package com.youngdatafan.sqlbuilder.util;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.DateFormatType;
import com.youngdatafan.sqlbuilder.enums.TimeUnitType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 配置文件工具类.
 *
 * @author yinkaifeng
 * @since 2021-08-02 9:00 上午
 */
public class PropertyUtils {
    /**
     * 日期格式前缀.
     */
    private static final String DATE_FORMAT = "dateformat.";

    /**
     * 时间单位前缀.
     */
    private static final String TIME_UNIT = "timeUnit.";

    /**
     * 数据库配置信息.
     */
    private static volatile Properties properties;

    /**
     * 加载配置文件.
     */
    static {
        String filename = "database.properties";
        InputStream is = null;
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            is = resource.getInputStream();
            properties = new Properties();
            properties.load(is);
        } catch (Exception e) {
            throw new SQLBuildException("加载数据库配置文件失败", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 根据日期格式枚举类型获取对应数据库日期格式字符串.
     *
     * @param databaseType   数据库类型
     * @param dateFormatType 日期格式枚举类型
     * @return 对应数据库日期格式字符串
     */
    public static String getDatabaseDateFormat(DatabaseType databaseType, DateFormatType dateFormatType) {
        String key = DATE_FORMAT + databaseType.getCode() + "." + getDateFormatTypePropertyCode(dateFormatType);
        String value = PropertyUtils.getValue(key);
        if ("".equals(value)) {
            throw new SQLBuildException(databaseType.getCode() + "暂不支持" + dateFormatType.getName() + "时间格式");
        }
        return value;
    }

    /**
     * 根据统一代码获取对应数据库日期格式字符串.
     *
     * @param databaseType 数据库类型
     * @param str          统一代码
     * @return 对应数据库日期格式字符串
     */
    public static String getDatabaseDateFormat(DatabaseType databaseType, String str) {
        final String code = str.replaceAll("'", "");
        DateFormatType dateFormatType = DateFormatType.getEnumByCode(code);
        return getDatabaseDateFormat(databaseType, dateFormatType);
    }

    /**
     * 根据时间类型枚举获取数据库时间单位字符串.
     *
     * @param databaseType 时间类型枚举
     * @param type         数据库类型
     * @return 数据库时间单位字符串
     */
    public static String getDatabaseTimeUnit(DatabaseType databaseType, TimeUnitType type) {
        String key = TIME_UNIT + databaseType.getCode() + "." + type.getCode();
        String value = PropertyUtils.getValue(key);
        if ("".equals(value)) {
            throw new SQLBuildException(databaseType.getCode() + "数据库暂不支持" + type.getCode() + "时间单位");
        }
        return value;
    }


    /**
     * 根据日期格式获取配置文件对应的code.
     *
     * @param dateFormatType 日期格式枚举
     * @return 配置文件对应的code
     */
    private static String getDateFormatTypePropertyCode(DateFormatType dateFormatType) {
        switch (dateFormatType) {
            case YYYY_MM_DD_HH24_MI_SS:
                return "1";
            case YYYYMMDDHH24MISS:
                return "2";
            case YYYY_MM_DD:
                return "3";
            case YYYYMMDD:
                return "4";
            case HH24_MI_SS:
                return "5";
            case HH24MISS:
                return "6";
            default:
                return "";
        }
    }

    /**
     * 根据键获取字符串值.
     *
     * @param key 键
     * @return 值
     */
    private static String getValue(String key) {
        Object o = properties.get(key);
        if (Objects.nonNull(o)) {
            return String.valueOf(o);
        }
        return "";
    }
}
