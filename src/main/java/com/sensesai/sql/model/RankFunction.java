package com.sensesai.sql.model;

import com.sensesai.sql.enums.FunctionType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 排名函数.
 *
 * @author yinkaifeng
 * @since 2021-08-27 10:32 上午
 */
@Getter
public final class RankFunction extends AbstractFunction {
    /**
     * 函数类型.
     */
    private FunctionType functionType;

    /**
     * 函数order by参数.
     */
    private List<Model> orderList = new ArrayList<>();

    /**
     * 函数partition by参数.
     */
    private List<Model> partitionList = new ArrayList<>();

    private RankFunction(FunctionType functionType, List<Model> partitionList, List<Model> orderList) {
        this.functionType = functionType;
        if (partitionList != null && partitionList.size() > 0) {
            this.partitionList.addAll(partitionList);
        }
        if (orderList != null && orderList.size() > 0) {
            this.orderList.addAll(orderList);
        }
    }

    /**
     * rank排名函数.
     *
     * @param partitionByParamList partition by参数
     * @param orderByParamList     order by参数
     * @return 函数模型对象
     */
    public static RankFunction rank(List<Model> partitionByParamList, List<Model> orderByParamList) {
        return new RankFunction(FunctionType.RANK, partitionByParamList, orderByParamList);
    }

    /**
     * dense_rank排名函数.
     *
     * @param partitionByParamList partition by参数
     * @param orderByParamList     order by参数
     * @return 函数模型对象
     */
    public static RankFunction denseRank(List<Model> partitionByParamList, List<Model> orderByParamList) {
        return new RankFunction(FunctionType.DENSE_RANK, partitionByParamList, orderByParamList);
    }

    /**
     * row_number排名函数.
     *
     * @param partitionByParamList partition by参数
     * @param orderByParamList     order by参数
     * @return 函数模型对象
     */
    public static RankFunction rowNumber(List<Model> partitionByParamList, List<Model> orderByParamList) {
        return new RankFunction(FunctionType.ROW_NUMBER, partitionByParamList, orderByParamList);
    }

    @Override
    public FunctionType getType() {
        return functionType;
    }
}
