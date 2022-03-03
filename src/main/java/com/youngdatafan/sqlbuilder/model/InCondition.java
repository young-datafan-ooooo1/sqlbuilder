package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.Op;
import com.youngdatafan.sqlbuilder.util.AssertUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * in或not in条件.
 *
 * @author yinkaifeng
 * @since 2021-07-14 4:43 下午
 */
@Getter
public final class InCondition extends Condition {
    /**
     * 匹配方式.
     */
    private final Op op;

    /**
     * 第一个值.
     */
    private final Model left;


    /**
     * 第二个值，针对 in 和 not in有多个的情况.
     */
    private final List<Model> rightList;

    private InCondition(Op op, Model left, Model... rights) {
        AssertUtils.notEmpty(left, op.getCode() + "左值不能为空");
        this.op = op;
        this.left = left;
        List<Model> rightList = new ArrayList<>();
        for (Model right : rights) {
            AssertUtils.notEmpty(right, op.getCode() + "值不能为空");
            rightList.add(right);
        }
        this.rightList = rightList;
    }

    /**
     * in条件.
     *
     * @param left   左边模型
     * @param rights 右边模型
     * @return in条件对象
     */
    public static InCondition in(Model left, Model... rights) {
        return new InCondition(Op.IN, left, rights);
    }

    /**
     * not in条件对象.
     *
     * @param left   左边模型
     * @param rights 右边模型
     * @return not in条件对象
     */
    public static InCondition notIn(Model left, Model... rights) {
        return new InCondition(Op.NOT_IN, left, rights);
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        StringBuilder sql = new StringBuilder();
        sql.append(left.getDatabaseSql(databaseType)).append(" ");
        sql.append(op.getCode()).append(" (");
        int size = rightList.size();
        for (int i = 0; i < size; i++) {
            sql.append(rightList.get(i).getDatabaseSql(databaseType));
            if (i != size - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        return sql.toString();
    }
}
