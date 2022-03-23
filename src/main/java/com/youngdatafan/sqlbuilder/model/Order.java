package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import com.youngdatafan.sqlbuilder.enums.SortType;
import com.youngdatafan.sqlbuilder.util.AssertUtils;
import lombok.Getter;

/**
 * order by模型.
 *
 * @author yinkaifeng
 * @since 2021-07-15 5:38 下午
 */
@Getter
public final class Order implements Model {
    /**
     * 排序类型.
     */
    private final SortType sortType;

    /**
     * 排序字段.
     */
    private final Model sortModel;

    private Order(SortType sortType, Model sortModel) {
        AssertUtils.notNull(sortType, "排序类型不能为空");
        AssertUtils.notNull(sortModel, "排序字段不能为空");
        this.sortType = sortType;
        this.sortModel = sortModel;
    }

    /**
     * 获取order对象.
     *
     * @param sortType  排序类型枚举
     * @param sortModel 排序模型
     * @return order对象
     */
    public static Order getOrder(SortType sortType, Model sortModel) {
        return new Order(sortType, sortModel);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.ORDER;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return sortModel.getDatabaseSql(databaseType) + " " + sortType.getCode();
    }
}
