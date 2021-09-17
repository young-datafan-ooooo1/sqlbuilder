package com.sensesai.sql.util;

import com.sensesai.sql.enums.DateFormatType;

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

    private static final Pattern VARCHAR = Pattern.compile("'(\\w\\S){0,}'");

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
}
