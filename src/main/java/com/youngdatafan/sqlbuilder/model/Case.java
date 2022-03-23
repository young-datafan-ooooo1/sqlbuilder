package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.constant.SQLConstant;
import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import com.youngdatafan.sqlbuilder.util.AssertUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * case when模型.
 *
 * @author yinkaifeng
 * @since 2021-07-20 5:16 下午
 */
@Getter
public class Case implements Model {
    /**
     * when条件值.
     */
    private final List<Model> whenValue = new ArrayList<>();

    /**
     * then条件值.
     */
    private final List<Model> thenValue = new ArrayList<>();

    /**
     * else值.
     */
    private Model elseValue = CustomSql.getCustomSql("NULL");

    /**
     * 添加when then.
     *
     * @param when when模型
     * @param then then模型
     */
    public void addWhenThen(Model when, Model then) {
        AssertUtils.notNull(when, "when不能为空");
        AssertUtils.notNull(then, "then不能为空");
        this.whenValue.add(when);
        this.thenValue.add(then);
    }

    /**
     * 添加else.
     *
     * @param elseValue else模型
     */
    public void addElse(Model elseValue) {
        AssertUtils.notNull(elseValue, "else不能为空");
        this.elseValue = elseValue;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.CASE;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        StringBuilder sql = new StringBuilder();
        sql.append("(").append(SQLConstant.CASE).append(" ");
        int size = whenValue.size();
        for (int i = 0; i < size; i++) {
            sql.append(SQLConstant.WHEN).append(" ");
            sql.append(whenValue.get(i).getDatabaseSql(databaseType)).append(" ");
            sql.append(SQLConstant.THEN).append(" ");
            sql.append(thenValue.get(i).getDatabaseSql(databaseType)).append(" ");
        }
        sql.append(SQLConstant.ELSE).append(" ");
        sql.append(elseValue.getDatabaseSql(databaseType)).append(" ");
        sql.append(SQLConstant.END).append(")");
        return sql.toString();
    }
}
