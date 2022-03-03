package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.Cp;
import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.ModelType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.util.AssertUtils;
import com.youngdatafan.sqlbuilder.util.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件.
 *
 * @author yinkaifeng
 * @since 2021-09-03 3:38 下午
 */
@Getter
public final class Conditions implements Model {
    /**
     * 条件组合方式.
     */
    private Cp cp;

    /**
     * 条件.
     */
    private Condition condition;

    /**
     * 条件组.
     */
    private List<Conditions> list;

    private Conditions(Cp cp, Condition condition, List<Conditions> list) {
        AssertUtils.notNull(cp, "条件组合方式不能为空");
        this.cp = cp;
        this.condition = condition;
        this.list = list;
    }

    /**
     * 获取条件对象.
     *
     * @return 条件对象
     */
    public static Conditions getInstance() {
        return new Conditions(Cp.NONE, null, new ArrayList<>());
    }

    /**
     * 获取条件对象.
     *
     * @param cp 组合方式枚举类型
     * @return 条件对象
     */
    public static Conditions getInstance(Cp cp) {
        AssertUtils.notEmpty(cp, "组合方式不能为空");
        return new Conditions(cp, null, new ArrayList<>());
    }

    /**
     * 获取条件对象.
     *
     * @param cp        组合方式枚举类型
     * @param condition 条件
     * @return 条件对象
     */
    public static Conditions getInstance(Cp cp, Condition condition) {
        AssertUtils.notEmpty(cp, "组合方式不能为空");
        AssertUtils.notEmpty(condition, "条件不能为空");
        return new Conditions(cp, condition, null);
    }

    /**
     * 获取条件对象.
     *
     * @param cp     组合方式枚举类型
     * @param arrays 条件数组
     * @return 条件对象
     */
    public static Conditions getInstance(Cp cp, Conditions... arrays) {
        AssertUtils.notEmpty(cp, "组合方式不能为空");
        AssertUtils.notEmpty(arrays, "条件不能为空");
        List<Conditions> list = new ArrayList<>();
        for (Conditions data : arrays) {
            list.add(data);
        }
        return new Conditions(cp, null, list);
    }

    /**
     * 添加and条件.
     *
     * @param condition 条件
     * @return 当前对象
     */
    public Conditions and(Condition condition) {
        list.add(new Conditions(Cp.AND, condition, null));
        return this;
    }

    /**
     * 添加or条件.
     *
     * @param condition 条件
     * @return 当前对象
     */
    public Conditions or(Condition condition) {
        list.add(new Conditions(Cp.AND, condition, null));
        return this;
    }

    /**
     * 添加条件.
     *
     * @param array 条件
     * @return 当前对象
     */
    public Conditions add(Conditions... array) {
        for (Conditions conditions : array) {
            list.add(conditions);
        }
        return this;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.CONDITION;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return buildSql(databaseType, true, true, this);
    }

    /**
     * 构建数据库标准sql.
     *
     * @param databaseType 数据库类型
     * @param isOuter      是否最外层的循环，最外层的循环不加上括号
     * @param isFirst      是否第一个，列表中的第一个是不需要加上操作符号的，外层已经加了
     * @param conditions   条件对象
     * @return 数据库标准sql
     */
    private String buildSql(DatabaseType databaseType, boolean isOuter, boolean isFirst, Conditions conditions) {
        if (conditions.getCondition() != null && Utils.isNotEmpty(conditions.getList())) {
            throw new SQLBuildException("只能是单个或多个条件中的一种");
        }
        StringBuilder sql = new StringBuilder();
        if (!isFirst) {
            //非列表中的第一个需要组合方式必须为and或or
            if (conditions.getCp() != Cp.AND
                    && conditions.getCp() != Cp.OR) {
                throw new SQLBuildException("组合方式必须是and或or");
            }
            sql.append(" " + conditions.getCp().getCode() + " ");
        }
        if (conditions.getCondition() != null) {
            sql.append(conditions.getCondition().getDatabaseSql(databaseType));
        }
        if (Utils.isNotEmpty(conditions.getList())) {
            int size = conditions.getList().size();
            boolean flag = true;
            if (!isOuter) {
                sql.append("(");
            }
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    flag = false;
                }
                sql.append(buildSql(databaseType, false, flag, conditions.getList().get(i)));
            }
            if (!isOuter) {
                sql.append(")");
            }
        }
        return sql.toString();
    }
}
