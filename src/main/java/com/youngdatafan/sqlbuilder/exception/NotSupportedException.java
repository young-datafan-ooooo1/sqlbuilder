package com.youngdatafan.sqlbuilder.exception;

import lombok.Data;

/**
 * 函数暂不支持异常.
 *
 * @author yinkaifeng
 * @since 2021-09-13 10:01 上午
 */
@Data
public class NotSupportedException extends RuntimeException {

    /**
     * 提示信息.
     */
    private String message;

    public NotSupportedException(final String message) {
        super(message);
        this.message = message;
    }

    public NotSupportedException(final String message, Throwable e) {
        super(message, e);
        this.message = message;
    }
}
