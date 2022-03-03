package com.youngdatafan.sqlbuilder.enums;

import lombok.Getter;

/**
 * 函数类型.
 *
 * @author yinkaifeng
 * @since 2021-07-14 6:39 下午
 */
@Getter
public enum FunctionType {
    ADD("+", "加"),
    SUB("-", "减"),
    MUL("*", "乘"),
    DIV("/", "除"),
    POWER("power", "求数的n次方"),
    PERCENTAGE("percentage", "百分比"),
    NOT("not", "取反"),
    E("e", "e的值"),
    PI("pi", "pi的值"),
    EXP("exp", "e的X次方"),
    EXP2("exp2", "2的X次方"),
    EXP10("exp10", "10的次方"),
    LOG("log", "log以e为底的对数值"),
    LOG2("log2", "log以2为底的对数值"),
    LOG10("log10", "log以10为底的对数值"),
    SQRT("sqrt", "开平方"),
    CBRT("cbrt", "开立方"),

    COUNT("count", "计算"),
    COUNT_DISTINCT("count_distinct", "去重计算"),
    MAX("max", "最大值"),
    MIN("min", "最小值"),
    SUM("sum", "求和"),
    AVG("avg", "求平均值"),
    WEIGHTED_MEAN("weighted_mean", "加权平均"),
    MEDIAN("median", "中位数"),
    STDDEV_SAMP("stddev_samp", "标准差"),
    VAR_SAMP("var_samp", "方差"),

    ABS("abs", "求绝对值"),
    MOD("mod", "取余"),
    CEIL("ceil", "向上取整"),
    FLOOR("floor", "向下取整"),
    ROUND("round", "四舍五入"),
    RAND("rand", "生成一个随机数值"),

    SIGN("sign", "参数的符号"),
    LENGTH("length", "计算字符串长度"),
    CONCAT("concat", "字符串拼接"),
    UPPER("upper", "字符串转大写"),
    LOWER("lower", "字符串转小写"),
    TRIM("trim", "删除两侧空格"),
    LTRIM("ltrim", "删除左侧空格"),
    RTRIM("rtrim", "删除右侧空格"),
    REPLACE("replace", "替换字符串"),
    SUBSTRING("substr", "截取字符串"),
    LEFT_SUBSTR("lsubstr", "从左侧截取字符串"),
    RIGHT_SUBSTR("rsubstr", "从右侧截取字符串"),
    INSTR("instr", "查找字符串位置"),
    GROUP_CONCAT("group_concat", "分组拼接"),

    DATE_TO_STRING("to_char", "日期转字符"),
    STRING_TO_DATE("to_date", "字符串转日期"),
    TIMESTAMP_TO_STRING("timestampToChar", "时间戳转字符串"),
    STRING_TO_TIMESTAMP("toTimestamp", "字符串转时间戳"),

    DATE_DIFF("datediff", "计算两个日期间隔"),
    ADD_DATE_TIME("addDateTime", "加日期时间"),
    SUB_DATE_TIME("subDateTime", "减日期时间"),

    RANK("rank", "排名函数"),
    DENSE_RANK("denseRank", "排名函数(将某字段按照顺序依次添加行号)"),
    ROW_NUMBER("rowNumber", "排名函数(是的排序数字是连续的)"),

    YEAR_BEGIN("year_begin", "年初"),
    YEAR_END("year_end", "年末"),
    QUARTER_BEGIN("quarter_begin", "季初"),
    QUARTER_END("quarter_end", "季末"),
    MONTH_BEGIN("month_begin", "月初"),
    MONTH_END("month_end", "月末"),
    WEEK_BEGIN("week_begin", "周初"),
    WEEK_END("week_end", "周末"),

    CURRENT_TIMESTAMP("current_timestamp", "当前日期时间"),
    CURRENT_DATE("current_date", "当前日期"),
    CURRENT_TIME("current_time", "当前时间"),

    CURRENT_YEAR("current_year", "当前年"),
    CURRENT_QUARTER("current_quarter", "当前季度"),
    CURRENT_MONTH("current_month", "当前月"),
    CURRENT_WEEK("current_week", "当前周"),
    CURRENT_DAY("current_day", "当前日"),
    YESTERDAY("yesterday", "昨日"),
    YEAR("year", "年份"),
    QUARTER("quarter", "季度"),
    MONTH("month", "月"),
    WEEKDAY("weekday", "周天数"),
    DAY("day", "天"),
    HOUR("hour", "小时"),
    MINUTE("minute", "分钟"),
    SECOND("second", "秒"),
    YEAR_MONTH("year_month", "年月"),

    DECODE("decode", ""),
    MONTHS_BETWEEN("months_between", ""),

    NUMBER_CHAR("number_char", "转成字符串"),
    TO_DECIMAL("to_decimal", "转成小数"),
    TO_INT("to_int", "转成整数");

    /**
     * 代码.
     */
    private final String code;

    /**
     * 名称.
     */
    private final String name;

    FunctionType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举值.
     *
     * @param code 代码
     * @return 枚举值
     */
    public static FunctionType getEnumByCode(String code) {
        FunctionType[] values = FunctionType.values();
        for (FunctionType val : values) {
            if (val.getCode().equalsIgnoreCase(code)) {
                return val;
            }
        }
        return null;
    }
}
