package com.youngdatafan.sqlbuilder.model;

import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.Op;
import com.youngdatafan.sqlbuilder.util.AssertUtils;
import lombok.Getter;


/**
 * 两个值的条件.
 *
 * @author yinkaifeng
 * @since 2021-07-14 4:43 下午
 */
@Getter
public final class BinaryCondition extends Condition {
    /**
     * 匹配方式.
     */
    private final Op op;

    /**
     * 第一个值.
     */
    private final Model left;

    /**
     * 第二个值.
     */
    private final Model right;

    private BinaryCondition(Op op, Model left, Model right) {
        AssertUtils.notNull(op, "条件匹配方式不能为空");
        AssertUtils.notNull(left, op.getCode() + "条件左值不能为空");
        if (op != Op.IS_NULL && op != Op.NOT_NULL) {
            AssertUtils.notNull(right, op.getCode() + "条件右值不能为空");
        }
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * 小于条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition lessThan(Model left, Model right) {
        return new BinaryCondition(Op.LESS_THAN, left, right);
    }

    /**
     * 小于等于条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition lessThanOrEqual(Model left, Model right) {
        return new BinaryCondition(Op.LESS_THAN_OR_EQUAL, left, right);
    }

    /**
     * 大于等于条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition greaterThan(Model left, Model right) {
        return new BinaryCondition(Op.GREATER_THAN, left, right);
    }

    /**
     * 大于等于条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition greaterThanOrEqual(Model left, Model right) {
        return new BinaryCondition(Op.GREATER_THAN_OR_EQUAL, left, right);
    }

    /**
     * 等于条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition equal(Model left, Model right) {
        return new BinaryCondition(Op.EQUAL, left, right);
    }

    /**
     * 不等于条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition notEqual(Model left, Model right) {
        return new BinaryCondition(Op.NOT_EQUAL, left, right);
    }

    /**
     * 匹配条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition like(Model left, Model right) {
        return new BinaryCondition(Op.LIKE, left, right);
    }

    /**
     * 不匹配条件.
     *
     * @param left  左值
     * @param right 右值
     * @return 条件对象
     */
    public static BinaryCondition notLike(Model left, Model right) {
        return new BinaryCondition(Op.NOT_LIKE, left, right);
    }

    /**
     * 是否为null条件.
     *
     * @param left 左值
     * @return 条件对象
     */
    public static BinaryCondition isNull(Model left) {
        return new BinaryCondition(Op.IS_NULL, left, null);
    }

    /**
     * 是否不为null条件.
     *
     * @param left 左值
     * @return 条件对象
     */
    public static BinaryCondition isNotNull(Model left) {
        return new BinaryCondition(Op.NOT_NULL, left, null);
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return left.getDatabaseSql(databaseType) + " "
                + op.getCode() + " "
                + (right == null ? "" : right.getDatabaseSql(databaseType));
    }
}
