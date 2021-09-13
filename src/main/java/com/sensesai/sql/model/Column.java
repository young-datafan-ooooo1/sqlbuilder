package com.sensesai.sql.model;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.ModelType;
import com.sensesai.sql.util.AssertUtils;
import com.sensesai.sql.util.KeywordUtils;
import com.sensesai.sql.util.Utils;
import lombok.Getter;

/**
 * 查询列.
 *
 * @author yinkaifeng
 * @since 2021-07-20 11:00 上午
 */
@Getter
public final class Column implements Model {
    /**
     * 别名.
     */
    private final String alias;

    /**
     * 模型.
     */
    private final Model model;

    private Column(String alias, Model model) {
        this.alias = alias;
        this.model = model;
    }

    /**
     * 获取column对象.
     *
     * @param alias 别名
     * @param model 查询列
     * @return column对象
     */
    public static Column getColumn(String alias, Model model) {
        AssertUtils.notNull(model, "查询列不能为空");
        return new Column(alias, model);
    }

    @Override
    public ModelType getModelType() {
        return ModelType.COLUMN;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        String sql = model.getDatabaseSql(databaseType);
        if (Utils.isNotEmpty(alias)) {
            sql = sql + " AS " + KeywordUtils.replaceKeyword(databaseType, alias);
        }
        return sql;
    }
}
