package com.sensesai.sql.model;

import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.exception.SQLBuildException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通函数.
 *
 * @author yinkaifeng
 * @since 2021-08-27 9:58 上午
 */
@Getter
public final class Function extends AbstractFunction {
    /**
     * 函数类型.
     */
    private final FunctionType functionType;

    /**
     * 函数参数.
     */
    private final List<Model> paramList = new ArrayList<>();

    private Function(FunctionType functionType) {
        this.functionType = functionType;
    }

    /**
     * 获取非排名函数.
     *
     * @param functionType 函数类型枚举
     * @param models       函数参数
     * @return 函数模型对象
     */
    public static Function getFunction(FunctionType functionType, Model... models) {
        if (functionType == FunctionType.RANK
                || functionType == FunctionType.DENSE_RANK
                || functionType == FunctionType.ROW_NUMBER) {
            throw new SQLBuildException("请使用排名函数的获取方法");
        }
        Function function = new Function(functionType);
        for (Model model : models) {
            function.getParamList().add(model);
        }
        return function;
    }

    @Override
    public FunctionType getType() {
        return functionType;
    }
}
