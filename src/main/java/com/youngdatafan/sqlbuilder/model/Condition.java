package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.ModelType;

/**
 * 条件模型父类.
 *
 * @author yinkaifeng
 * @since 2021-08-26 4:14 下午
 */
public abstract class Condition implements Model {

    @Override
    public ModelType getModelType() {
        return ModelType.CONDITION;
    }
}
