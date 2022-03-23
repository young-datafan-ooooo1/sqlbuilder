package com.youngdatafan.sqlbuilder.function;

import lombok.Data;

import java.util.List;

/**
 * 函数校验定义类.
 *
 * @author yinkaifeng
 * @since 2021-09-16 3:54 下午
 */
@Data
public class FunctionCheckDefinition {
    /**
     * 函数名.
     */
    private String name;

    /**
     * 参数最小个数.
     */
    private Integer min;

    /**
     * 参数最大个数.
     */
    private Integer max;

    /**
     * 函数返回值类型.
     */
    private String returnType;

    /**
     * 函数每个参数详细校验规则(如果该list小于参数总个数，那么之后的参数校验都基于该规则).
     */
    private List<CheckDetail> params;

    @Data
    public static class CheckDetail {
        /**
         * 允许数据类型数组.
         */
        private String[] checkRegexType;

        /**
         * 不匹配提示语.
         */
        private String checkMsg;
    }
}
