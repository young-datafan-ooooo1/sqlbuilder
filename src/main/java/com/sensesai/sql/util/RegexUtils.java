package com.sensesai.sql.util;

import com.sensesai.sql.enums.DateFormatType;
import com.sensesai.sql.enums.TimeUnitType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类.
 *
 * @author yinkaifeng
 * @since 2021-07-30 2:07 下午
 */
public class RegexUtils {

    private static final Pattern INT = Pattern.compile("(-)?(\\d){1,}");

    private static final Pattern NUMBER = Pattern.compile("(-)?((\\d{1,})|(([1-9]{0,})\\d.\\d{1,}))");

    private static final Pattern VARCHAR = Pattern.compile("'(\\w|\\s|:|-){0,}'");

    private static final Pattern DATETIME_VARCHAR = Pattern.compile("'((\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})|(\\d{14}))'");

    private static final Pattern DATE_VARCHAR = Pattern.compile("'((\\d{8})|(\\d{4}-\\d{2}-\\d{2}))'");

    private static final Pattern TIME_VARCHAR = Pattern.compile("'((\\d{6})|(\\d{2}:\\d{2}:\\d{2}))'");

    /**
     * 是否匹配整数.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchInt(String value) {
        Matcher matcher = INT.matcher(value);
        return matcher.matches();
    }

    /**
     * 是否匹配数字.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchNumber(String value) {
        Matcher matcher = NUMBER.matcher(value);
        return matcher.matches();
    }

    /**
     * 是否匹配字符串.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchVarchar(String value) {
        Matcher matcher = VARCHAR.matcher(value);
        return matcher.matches();
    }

    /**
     * 是否匹配日期支持的自定义日期格式.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchDateFormat(String value) {
        if (value == null) {
            return false;
        }
        if (!(value.startsWith("'") && value.endsWith("'"))) {
            return false;
        }
        String str = value.replaceAll("'", "");
        DateFormatType[] values = DateFormatType.values();
        for (DateFormatType type : values) {
            if (type.getCode().equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否匹配日期支持的自定义日期格式.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchTimeUnit(String value) {
        if (value == null) {
            return false;
        }
        if (!(value.startsWith("'") && value.endsWith("'"))) {
            return false;
        }
        String str = value.replaceAll("'", "");
        TimeUnitType[] values = TimeUnitType.values();
        for (TimeUnitType type : values) {
            if (type.getCode().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否匹配日期时间格式字符串.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchDateTimeVarchar(String value) {
        Matcher matcher = DATETIME_VARCHAR.matcher(value);
        return matcher.matches();
    }

    /**
     * 是否匹配日期格式字符串.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchDateVarchar(String value) {
        Matcher matcher = DATE_VARCHAR.matcher(value);
        return matcher.matches();
    }

    /**
     * 是否匹配时间格式字符串.
     *
     * @param value 值
     * @return 是否匹配
     */
    public static boolean isMatchTimeVarchar(String value) {
        Matcher matcher = TIME_VARCHAR.matcher(value);
        return matcher.matches();
    }
}
