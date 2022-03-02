package com.youngdatafan.sqlbuilder.exception;

import lombok.Data;

/**
 * sql构建异常类.
 *
 * @author yinkaifeng
 * @since 2021-09-13 10:01 上午
 */
@Data
public class SQLBuildException extends RuntimeException {
    /**
     * 提示信息.
     */
    private String message;

    public SQLBuildException(final String message) {
        super(message);
        this.message = message;
    }

    public SQLBuildException(final String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
