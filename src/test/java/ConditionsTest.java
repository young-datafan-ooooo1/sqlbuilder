import com.youngdatafan.sqlbuilder.enums.Cp;
import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.enums.Op;
import com.youngdatafan.sqlbuilder.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 条件测试类.
 *
 * @author yinkaifeng
 * @since 2021-09-06 2:06 下午
 */
public class ConditionsTest {

    private Table table;
    private Field name, salary;
    private Conditions conditions;

    @Before
    public void before() {
        table = Table.getOriginalTable(null, "test", "t");
        name = Field.getField(table, "name");
        salary = Field.getField(table, "salary");
    }

    @After
    public void after() {
        System.out.println(conditions.getDatabaseSql(DatabaseType.MYSQL));
    }

    /**
     * a.name LIKE '%s%'
     */
    @Test
    public void test1() {
        conditions = getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%s%'"));
    }

    /**
     * a.name like '%s%'
     * and a.salary>10
     */
    @Test
    public void test2() {
        conditions = Conditions.getInstance(Cp.NONE,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%s%'")),
                getSingle(Cp.AND, Op.GREATER_THAN, salary, CustomSql.getCustomSql("10"))
        );
    }

    /**
     * a.name like '%s%'
     * or (a.name like 'z%' and a.salary>10)
     */
    @Test
    public void test3() {
        Conditions first = getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%s%'"));

        Conditions second = Conditions.getInstance(Cp.OR,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'z%'")),
                getSingle(Cp.AND, Op.GREATER_THAN, salary, CustomSql.getCustomSql("10"))
        );

        conditions = Conditions.getInstance(Cp.NONE, first, second);
    }

    /**
     * (a.name like '%s%' and a.salary<10)
     * or (a.name like 'z%' and a.salary>10)
     */
    @Test
    public void test4() {
        Conditions first = Conditions.getInstance(Cp.OR,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%s%'")),
                getSingle(Cp.AND, Op.LESS_THAN, salary, CustomSql.getCustomSql("10"))
        );

        Conditions second = Conditions.getInstance(Cp.OR,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'z%'")),
                getSingle(Cp.AND, Op.GREATER_THAN, salary, CustomSql.getCustomSql("10"))
        );

        conditions = Conditions.getInstance(Cp.NONE, first, second);
    }

    /**
     * (a.name like '%s%' or (a.name like 'z%' and a.salary>10))
     */
    @Test
    public void test5() {
        Conditions second = Conditions.getInstance(Cp.OR,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'z%'")),
                getSingle(Cp.AND, Op.GREATER_THAN, salary, CustomSql.getCustomSql("10"))
        );

        Conditions first = Conditions.getInstance(Cp.NONE,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%s%'")),
                second
                );

        conditions = Conditions.getInstance(Cp.NONE, first);
    }

    /**
     * a.name like '%s%'
     * and (   a.name like 'z%'
     * or (a.name like '%g' and a.salary>10))
     */
    @Test
    public void test6() {
        Conditions three = Conditions.getInstance(Cp.AND,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%g'")),
                getSingle(Cp.AND, Op.GREATER_THAN, salary, CustomSql.getCustomSql("10"))
        );

        Conditions second = Conditions.getInstance(Cp.AND,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'z%'")),
                three
        );

        Conditions first = Conditions.getInstance(Cp.NONE,
                getSingle(Cp.NONE, Op.LIKE, name, CustomSql.getCustomSql("'%s%'")),
                second
        );

        conditions = Conditions.getInstance(Cp.NONE, first);
    }


    private Conditions getSingle(Cp cp, Op op, Model left, Model... rights) {
        Condition condition = null;
        switch (op) {
            case EQUAL:
                condition = BinaryCondition.equal(left, rights[0]);
                break;
            case NOT_EQUAL:
                condition = BinaryCondition.notEqual(left, rights[0]);
                break;
            case GREATER_THAN:
                condition = BinaryCondition.greaterThan(left, rights[0]);
                break;
            case GREATER_THAN_OR_EQUAL:
                condition = BinaryCondition.greaterThanOrEqual(left, rights[0]);
                break;
            case LESS_THAN:
                condition = BinaryCondition.lessThan(left, rights[0]);
                break;
            case LESS_THAN_OR_EQUAL:
                condition = BinaryCondition.lessThanOrEqual(left, rights[0]);
                break;
            case LIKE:
                condition = BinaryCondition.like(left, rights[0]);
                break;
            case NOT_LIKE:
                condition = BinaryCondition.notLike(left, rights[0]);
                break;
            default:
                condition = null;
        }
        return Conditions.getInstance(cp, condition);
    }
}
