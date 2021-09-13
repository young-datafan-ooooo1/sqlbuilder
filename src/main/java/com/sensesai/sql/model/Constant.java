package com.sensesai.sql.model;

import com.sensesai.sql.enums.DataType;
import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.ModelType;
import com.sensesai.sql.util.ConstantUtils;
import lombok.Getter;

/**
 * 常量.
 *
 * @author yinkaifeng
 * @since 2021-07-14 1:58 下午
 */
@Getter
public final class Constant implements Model {
    /**
     * 数据类型.
     */
    private final DataType type;

    /**
     * 常量值.
     */
    private final String value;

    /**
     * 数据格式.
     */
    private final String format;

    /**
     * 数据长度.
     */
    private Integer length;

    /**
     * 数据精度.
     */
    private Integer precision;

    private Constant(DataType type, String value, String format, Integer length, Integer precision) {
        this.type = type;
        this.value = value;
        this.format = format;
        this.length = length;
        this.precision = precision;
    }

    /**
     * 获取常量类型.
     *
     * @param type      数据类型枚举
     * @param value     值
     * @param format    格式
     * @param length    长度
     * @param precision 精度
     * @return 常量对象
     */
    public static Constant getConstant(DataType type, String value, String format, Integer length, Integer precision) {
        return new Constant(type, value, format, length, precision);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.CONSTANT;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return ConstantUtils.putThreadLocalConstant(this);
    }

    /**
     * 获取原sql即常量未转变时的sql.
     *
     * @param databaseType 数据库类型
     * @return 原sql即常量未转变时的sql
     */
    public String getOriginalSql(DatabaseType databaseType) {
        return value;
    }
}
