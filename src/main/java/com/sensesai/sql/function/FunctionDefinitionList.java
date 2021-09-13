package com.sensesai.sql.function;

import java.util.List;
import lombok.Data;

/**
 * 函数定义列表.
 *
 * @author yinkaifeng
 * @since 2021-08-27 3:03 下午
 */
@Data
public class FunctionDefinitionList {
    /**
     * 函数列表.
     */
    private List<FunctionDefinition> functions;
}
