package com.youngdatafan.sqlbuilder.function;

import lombok.Data;

/**
 * 函数参数定义.
 *
 * @author yinkaifeng
 * @since 2021-08-27 4:55 下午
 */
@Data
public class FunctionParamDefinition {

    /**
     * 检查的表达式匹配类型(string：字符串，number：数字).
     */
    private String checkRegexType;

    /**
     * 检查的提示语句.
     */
    private String checkMsg;
}
