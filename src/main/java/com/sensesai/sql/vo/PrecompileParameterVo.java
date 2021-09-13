package com.sensesai.sql.vo;

import lombok.Data;

/**
 * 预编译参数.
 *
 * @author yinkaifeng
 * @since 2021-07-16 6:12 下午
 */
@Data
public class PrecompileParameterVo {
    /**
     * 转换后的类型值.
     */
    private Object value;

    /**
     * 数据类型.
     */
    private String dataType;

    /**
     * 数据字符串值.
     */
    private String dataValue;

    /**
     * 数据格式.
     */
    private String dataFormat;

    /**
     * 数据长度.
     */
    private Integer dataLength;

    /**
     * 数据精度.
     */
    private Integer dataPrecision;
}
