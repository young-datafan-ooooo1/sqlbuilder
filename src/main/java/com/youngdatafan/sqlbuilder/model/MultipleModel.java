package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import lombok.Getter;

import java.util.Objects;

/**
 * 多个模型对象.
 *
 * @author yinkaifeng
 * @since 2021-08-10 4:34 下午
 */
@Getter
public final class MultipleModel implements Model {

    private final Model[] models;

    private MultipleModel(Model[] models) {
        this.models = models;
    }

    /**
     * 获取多个模型对象.
     *
     * @param models 模型对象
     * @return 多个模型对象
     */
    public static MultipleModel getMultipleModel(Model... models) {
        return new MultipleModel(models);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.MULTIPLE_MODEL;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        if (Objects.isNull(models)) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        for (Model model : models) {
            sql.append(model.getDatabaseSql(databaseType)).append(" ");
        }
        return sql.toString();
    }
}
