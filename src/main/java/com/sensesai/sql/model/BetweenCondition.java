package com.sensesai.sql.model;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.util.AssertUtils;
import lombok.Getter;

/**
 * between条件.
 *
 * @author yinkaifeng
 * @since 2021-07-14 4:43 下午
 */
@Getter
public final class BetweenCondition extends Condition {
    /**
     * 第一个值.
     */
    private final Model first;

    /**
     * 第二个值.
     */
    private final Model second;

    /**
     * 第三个值，针对between and.
     */
    private final Model third;

    private BetweenCondition(Model first, Model second, Model third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * 获取between条件对象.
     *
     * @param first  第一个值
     * @param second 第二个值
     * @param third  第三个值
     * @return between条件对象.
     */
    public static BetweenCondition between(Model first, Model second, Model third) {
        AssertUtils.notNull(first, "between条件的第一个值不能为空");
        AssertUtils.notNull(first, "between条件的第二个值不能为空");
        AssertUtils.notNull(first, "between条件的第三个值不能为空");
        return new BetweenCondition(first, second, third);
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return first.getDatabaseSql(databaseType)
                + " BETWEEN "
                + second.getDatabaseSql(databaseType)
                + " AND " + third.getDatabaseSql(databaseType);
    }
}
