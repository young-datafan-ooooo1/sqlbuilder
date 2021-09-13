package com.sensesai.sql.model;

import com.sensesai.sql.constant.SQLConstant;
import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.ModelType;
import com.sensesai.sql.enums.SortType;
import com.sensesai.sql.exception.SQLBuildException;
import com.sensesai.sql.util.AssertUtils;
import com.sensesai.sql.util.ConstantUtils;
import com.sensesai.sql.util.Utils;
import com.sensesai.sql.vo.PrecompileParameterVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * sql语句.
 *
 * @author yinkaifeng
 * @since 2021-07-14 2:27 下午
 */
@Data
public class Query implements Model {
    /**
     * 别名.
     */
    private String alias = "";

    /**
     * 预编译参数.
     */
    private List<PrecompileParameterVo> precompileParameterVoList = new ArrayList<>();

    /**
     * 是否包含distinct.
     */
    private Boolean isDistinct = false;

    /**
     * 查询字段.
     */
    private List<Model> select = new ArrayList<>();

    /**
     * from语句片段.
     */
    private List<Model> from = new ArrayList<>();

    /**
     * join语句片段.
     */
    private List<Join> join = new ArrayList<>();

    /**
     * where语句片段.
     */
    private Conditions where;

    /**
     * group语句片段.
     */
    private List<Model> group = new ArrayList<>();

    /**
     * having语句片段.
     */
    private Conditions having;


    /**
     * order语句片段.
     */
    private List<Order> order = new ArrayList<>();

    /**
     * 添加查询列.
     *
     * @param alias 别名
     * @param model 查询列
     * @return 查询模型
     */
    public Query addColumn(String alias, Model model) {
        AssertUtils.notNull(model, "查询列不能为空");
        if (Utils.isNotEmpty(alias)) {
            Column column = Column.getColumn(alias, model);
            this.select.add(column);
        } else {
            this.select.add(model);
        }
        return this;
    }

    /**
     * 添加查询列.
     *
     * @param models 查询列
     * @return 查询模型
     */
    public Query addColumn(Model... models) {
        AssertUtils.notEmpty(models, "查询列不能为空");
        for (Model model : models) {
            this.select.add(model);
        }
        return this;
    }

    /**
     * 添加查询表.
     *
     * @param table 查询表
     * @return 查询模型
     */
    public Query addFrom(Table table) {
        this.from.add(table);
        return this;
    }

    /**
     * 添加子查询.
     *
     * @param query 子查询
     * @return 查询模型
     */
    public Query addFrom(Query query) {
        AssertUtils.notEmpty(query.getAlias(), "子查询别名不能为空");
        this.from.add(query);
        return this;
    }

    /**
     * 添加join.
     *
     * @param joins join模型
     * @return 查询模型
     */
    public Query addJoin(Join... joins) {
        AssertUtils.notEmpty(joins, "join不能为空");
        for (Join join : joins) {
            this.join.add(join);
        }
        return this;
    }

    /**
     * 设置where条件.
     *
     * @param where 条件
     * @return 查询模型
     */
    public Query setWHere(Conditions where) {
        AssertUtils.notNull(where, "where条件不能为空");
        this.where = where;
        return this;
    }


    /**
     * 添加group by.
     *
     * @param model 模型
     * @return 查询模型
     */
    public Query addGroupBy(Model model) {
        group.add(model);
        return this;
    }

    /**
     * 设置having条件.
     *
     * @param having 条件
     * @return 查询模型
     */
    public Query setHaving(Conditions having) {
        AssertUtils.notNull(having, "having条件不能为空");
        this.having = having;
        return this;
    }


    /**
     * 添加排序字段.
     *
     * @param sortType  排序方式
     * @param sortModel 排序字段
     * @return 查询模型
     */
    public Query addOrderBy(SortType sortType, Model sortModel) {
        Order orderModel = Order.getOrder(sortType, sortModel);
        order.add(orderModel);
        return this;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.QUERY;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return buildSql(databaseType);
    }

    /**
     * 获取子查询sql.
     *
     * @param databaseType 数据库类型
     * @return 查询模型
     */
    public String getChildQuerySql(DatabaseType databaseType) {
        if (alias == null || "".equals(alias.trim())) {
            throw new SQLBuildException("子查询必须有别名");
        }
        return "(" + getDatabaseSql(databaseType) + ") " + alias;
    }

    /**
     * 获取可执行sql.
     *
     * @param databaseType 数据库类型
     * @return 预编译sql
     */
    public String getExecuteSql(DatabaseType databaseType) {
        String sql = buildSql(databaseType);
        return replaceConstant(databaseType, true, sql);
    }

    /**
     * 获取预编译sql.
     *
     * @param databaseType 数据库类型
     * @return 预编译sql
     */
    public String getPreSql(DatabaseType databaseType) {
        String sql = buildSql(databaseType);
        return replaceConstant(databaseType, false, sql);
    }


    /**
     * 构建查询sql.
     *
     * @param databaseType 数据库类型
     * @return 查询sql
     */
    private String buildSql(DatabaseType databaseType) {
        StringBuilder sql = new StringBuilder();
        //拼接select
        sql.append(buildSelectSql(databaseType)).append(" ");
        //拼接from
        sql.append(toDatabaseSql(databaseType, SQLConstant.FROM, from)).append(" ");
        //拼接join
        sql.append(toDatabaseSql(databaseType, join));
        //拼接where
        sql.append(toDatabaseSql(databaseType, SQLConstant.WHERE, where)).append(" ");
        //拼接group by
        sql.append(toDatabaseSql(databaseType, SQLConstant.GROUP_BY, group)).append(" ");
        //拼接having
        sql.append(toDatabaseSql(databaseType, SQLConstant.HAVING, having)).append(" ");
        //拼接order by
        sql.append(toDatabaseSql(databaseType, SQLConstant.ORDER_BY, order)).append(" ");
        return sql.toString();
    }

    /**
     * 替换常量.
     *
     * @param databaseType 数据库类型
     * @param useValue     是否使用原值
     * @param sql          常量特殊处理过的sql
     * @return 对应的sql
     */
    private String replaceConstant(DatabaseType databaseType, boolean useValue, final String sql) {
        String s = sql;
        for (Map.Entry<String, PrecompileParameterVo> entry : ConstantUtils.getThreadLocalConstant().entrySet()) {
            if (useValue) {
                s = s.replace(entry.getKey(), ConstantUtils.convertDatabaseValue(databaseType, entry.getValue()));
            } else {
                s = s.replace(entry.getKey(), "?");
                precompileParameterVoList.add(entry.getValue());
            }
        }
        return s;
    }

    /**
     * 构建select部分.
     *
     * @param databaseType 数据库类型
     * @return 标准sql
     */
    private String buildSelectSql(DatabaseType databaseType) {
        StringBuilder sql = new StringBuilder();
        sql.append(SQLConstant.SELECT).append(" ");
        if (isDistinct) {
            sql.append(SQLConstant.DISTINCT).append(" ");
        }
        if (select.isEmpty()) {
            sql.append("*");
        } else {
            appendList(databaseType, sql, select);
        }
        return sql.toString();
    }


    /**
     * 将查询语句的每个部分转化为对于的sql.
     *
     * @param databaseType 数据库类型
     * @param keyword      数据库关键字
     * @param list         集合模型
     * @return 标准sql
     */
    private String toDatabaseSql(DatabaseType databaseType, String keyword, List list) {
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder sql = new StringBuilder(keyword + " ");
        appendList(databaseType, sql, list);
        return sql.toString();
    }

    /**
     * 将条件转为数据库标准sql.
     *
     * @param databaseType 数据库类型
     * @param keyword      数据库关键字
     * @param conditions   条件模型
     * @return 标准sql
     */
    private String toDatabaseSql(DatabaseType databaseType, String keyword, Conditions conditions) {
        if (conditions == null) {
            return "";
        }
        return keyword + " " + conditions.getDatabaseSql(databaseType);
    }

    /**
     * 转为数据库标准sql.
     *
     * @param databaseType 数据库类型
     * @param joinList     join模型
     * @return 标准sql
     */
    private String toDatabaseSql(DatabaseType databaseType, List<Join> joinList) {
        if (joinList.isEmpty()) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        for (Join join : joinList) {
            sql.append(join.getDatabaseSql(databaseType)).append(" ");
        }
        return sql.toString();
    }

    /**
     * 拼接list集合模型，使用逗号分割.
     *
     * @param databaseType 数据库类型
     * @param sql          StringBuilder对象
     * @param list         集合模型
     */
    private void appendList(DatabaseType databaseType, StringBuilder sql, List list) {
        Model model;
        for (int i = 0, size = list.size(); i < size; i++) {
            model = (Model) list.get(i);
            if (model instanceof Query) {
                sql.append(((Query) model).getChildQuerySql(databaseType));
            } else {
                sql.append(((Model) list.get(i)).getDatabaseSql(databaseType));
            }
            if (i != size - 1) {
                sql.append(",");
            }
        }
    }

    /**
     * 校验模型的类型是否满足给定的模型类型.
     *
     * @param model 模型
     * @param types 模型类型
     */
    private void checkModel(Model model, ModelType... types) {
        if (types != null && types.length > 0) {
            ModelType modelType = model.getModelType();
            boolean pass = false;
            for (ModelType type : types) {
                if (type == modelType) {
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                throw new SQLBuildException("暂不支持" + modelType + "模型类型");
            }
        }
    }
}
