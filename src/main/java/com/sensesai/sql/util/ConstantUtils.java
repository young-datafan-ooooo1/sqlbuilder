package com.sensesai.sql.util;

import com.sensesai.sql.constant.SQLConstant;
import com.sensesai.sql.enums.DataType;
import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.DateFormatType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.model.Constant;
import com.sensesai.sql.model.CustomSql;
import com.sensesai.sql.model.Function;
import com.sensesai.sql.vo.PrecompileParameterVo;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 常量工具类.
 *
 * @author yinkaifeng
 * @since 2021-08-17 2:37 下午
 */
public class ConstantUtils {
    /**
     * 存储常量值，方便后续使用.
     */
    private static final ThreadLocal<LinkedHashMap<String, PrecompileParameterVo>> PARAMETER
            = ThreadLocal.withInitial(LinkedHashMap::new);

    /**
     * 当前常量个数，方便后续使用.
     */
    private static final ThreadLocal<AtomicInteger> PARAMETER_NUM
            = ThreadLocal.withInitial(() -> new AtomicInteger(0));


    /**
     * 将常量转化为对应数据库标准sql.
     * 1、字符串需要加''
     * 2、日期、时间、时间戳得用对应的函数转换
     *
     * @param databaseType 数据库类型
     * @param dataType     数据类型
     * @param value        数据值
     * @param format       数据格式
     * @param length       数据长度
     * @param precision    数据精度
     * @return 数据库标准sql
     */
    public static String convertDatabaseValue(DatabaseType databaseType, DataType dataType, String value,
                                              String format, Integer length, Integer precision) {
        if (value == null) {
            return "null";
        }
        switch (dataType) {
            case STRING:
                return "'" + value + "'";
            case INT:
            case INTEGER:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case NUMBER:
                return value;
            case DATE:
            case TIME:
            case DATETIME:
                return Function.getFunction(FunctionType.STRING_TO_DATE,
                                CustomSql.getCustomSql("'" + value + "'"),
                                CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD_HH24_MI_SS.getCode()))
                        .getDatabaseSql(databaseType);
            default:
                throw new SQLBuildException("暂不支持" + dataType.getCode() + "数据类型");
        }
    }

    /**
     * 转化成数据库标准sql.
     *
     * @param databaseType 数据库类型
     * @param parameterVo  值
     * @return 数据库标准sql
     */
    public static String convertDatabaseValue(DatabaseType databaseType, PrecompileParameterVo parameterVo) {
        DataType dataType = DataType.getEnumByCode(parameterVo.getDataType());
        if (dataType == null) {
            throw new SQLBuildException("暂不支持" + dataType.getCode() + "数据类型");
        }
        return convertDatabaseValue(databaseType, dataType, parameterVo.getDataValue(),
                parameterVo.getDataFormat(), parameterVo.getDataLength(), parameterVo.getDataPrecision());
    }

    /**
     * 将常量转化为数据库对应的类型.
     *
     * @param dataType  数据类型
     * @param value     数据值
     * @param format    数据格式
     * @param length    数据长度
     * @param precision 数据精度
     * @return 数据库对应的类型
     */
    public static Object convertValue(DataType dataType, String value, String format,
                                      Integer length, Integer precision) {
        switch (dataType) {
            case STRING:
                return value;
            case INT:
            case INTEGER:
                return getInt(value);
            case BIGINT:
            case LONG:
                return getLong(value);
            case FLOAT:
                return getFloat(value);
            case DOUBLE:
                return getDouble(value);
            case NUMBER:
                if (length != null && length <= 18 && precision != null && precision == 0) {
                    return getLong(value);
                } else {
                    return getBigDecimal(value);
                }
            case DATE:
            case DATETIME:
                return getDate(value, format);
            case TIME:
                return getTime(value, format);
            default:
                throw new SQLBuildException("暂不支持" + dataType.getCode() + "数据类型");
        }
    }


    /**
     * 设置本地线程常量值.
     *
     * @param model 常量值
     * @return 键
     */
    public static String putThreadLocalConstant(Constant model) {
        final String key = getConstantReplace();
        PrecompileParameterVo parameter = new PrecompileParameterVo();
        parameter.setDataType(model.getType().getCode());
        parameter.setDataValue(model.getValue());
        parameter.setDataFormat(model.getFormat());
        parameter.setDataLength(model.getLength());
        parameter.setDataPrecision(model.getPrecision());
        parameter.setValue(convertValue(model.getType(), model.getValue(),
                model.getFormat(), model.getLength(), model.getPrecision()));
        PARAMETER.get().put(key, parameter);
        return key;
    }

    /**
     * 获取本地线程常量信息.
     *
     * @return 本地线程常量信息
     */
    public static Map<String, PrecompileParameterVo> getThreadLocalConstant() {
        return PARAMETER.get();
    }

    /**
     * 清除本地线程信息.
     */
    public static void cleanThreadLocalConstant() {
        PARAMETER.remove();
        PARAMETER_NUM.remove();
    }

    /**
     * 获取常量替换值.
     *
     * @return 常量替换值
     */
    private static String getConstantReplace() {
        return SQLConstant.CONSTANT_PREFIX
                + getFixedValue(PARAMETER_NUM.get().getAndIncrement(), 3)
                + SQLConstant.CONSTANT_SUFFIX;
    }

    /**
     * 获取定长字符串，不满长度前面补齐0.
     *
     * @param value 值
     * @param len   长度
     * @return 定长字符串
     */
    private static String getFixedValue(int value, int len) {
        String s = value + "";
        while (s.length() <= len) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * 字符串转short.
     *
     * @param value 字符串
     * @return short
     */
    public static Short getShort(String value) {
        try {
            return Short.valueOf(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成short", e);
        }
    }

    /**
     * 字符串转byte.
     *
     * @param value 字符串
     * @return byte
     */
    public static Byte getByte(String value) {
        try {
            return Byte.valueOf(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Byte", e);
        }
    }

    /**
     * 字符串转Int.
     *
     * @param value 字符串
     * @return Int
     */
    public static Integer getInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Int", e);
        }
    }

    /**
     * 字符串转Long.
     *
     * @param value 字符串
     * @return Long
     */
    public static Long getLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Long", e);
        }
    }

    /**
     * 字符串转Float.
     *
     * @param value 字符串
     * @return Float
     */
    public static Float getFloat(String value) {
        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Float", e);
        }
    }

    /**
     * 字符串转Double.
     *
     * @param value 字符串
     * @return Double
     */
    public static Double getDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Double", e);
        }
    }

    /**
     * 字符串转BigDecimal.
     *
     * @param value 字符串
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成BigDecimal", e);
        }
    }

    /**
     * 字符串转Date.
     *
     * @param value  字符串
     * @param format 格式
     * @return Date
     */
    public static Date getDate(String value, String format) {
        try {
            if (format != null && !"".equals(format.trim())) {
                return new Date(DateUtils.parseDate(value, format).getTime());
            }
            return new Date(DateUtils.parseDate(value,
                    "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd").getTime());
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Date", e);
        }
    }

    /**
     * 字符串转Time.
     *
     * @param value  字符串
     * @param format 格式
     * @return Time
     */
    public static Time getTime(String value, String format) {
        try {
            if (format != null && !"".equals(format.trim())) {
                return new Time(DateUtils.parseDate(value, format).getTime());
            }
            return new Time(DateUtils.parseDate(value,
                    "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd").getTime());
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Time", e);
        }
    }

    /**
     * 字符串转Timestamp.
     *
     * @param value  字符串
     * @param format 格式
     * @return Timestamp
     */
    public static Timestamp getTimestamp(String value, String format) {
        try {
            if (format != null && !"".equals(format.trim())) {
                return new Timestamp(DateUtils.parseDate(value, format).getTime());
            }
            return new Timestamp(DateUtils.parseDate(value,
                    "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd").getTime());
        } catch (Exception e) {
            throw new SQLBuildException(value + "不能转换成Timestamp", e);
        }
    }
}
