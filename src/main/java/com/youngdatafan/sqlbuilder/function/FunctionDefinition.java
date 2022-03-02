package com.youngdatafan.sqlbuilder.function;

import java.util.List;
import lombok.Data;

/**
 * 函数定义.
 *
 * @author yinkaifeng
 * @since 2021-08-27 10:41 上午
 */
@Data
public class FunctionDefinition {
    /**
     * 函数名称.
     */
    private String name;

    /**
     * 函数描述.
     */
    private String describe;

    /**
     * 对应数据函数名.
     */
    private String function;

    /**
     * 函数左包围符.
     */
    private String leftQuote = "(";

    /**
     * 函数右包围符.
     */
    private String rightQuote = ")";

    /**
     * 参数个数类型（fixed：固定，range：范围）.
     */
    private String paramSizeType = "fixed";

    /**
     * 参数最小个数.
     */
    private int paramMinSize;

    /**
     * 参数最大个数.
     */
    private int paramMaxSize;

    /**
     * 参数分割符，默认是逗号.
     */
    private String paramSplit = ",";

    /**
     * 函数参数.
     */
    private List<FunctionParamDefinition> params;
}
