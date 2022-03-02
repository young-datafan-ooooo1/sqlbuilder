import com.youngdatafan.sqlbuilder.enums.*;
import com.youngdatafan.sqlbuilder.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 查询测试类.
 *
 * @author yinkaifeng
 * @since 2021-09-07 9:30 上午
 */
public class QueryTest {
    private Schema schema;

    private Query query;

    private Table test;

    private Field id, salary, name, create_date, create_time, create_timestamp;

    @Before
    public void before() {
        schema = Schema.getSchema("");
        test = Table.getOriginalTable(schema, "test", "t0");

        id = test.addField("id");
        salary = test.addField("salary");
        name = test.addField("name");
        create_date = test.addField("create_date");
        create_time = test.addField("create_time");
        create_timestamp = test.addField("create_timestamp");

        query = new Query();
    }

    @After
    public void after() {
        System.out.println("mysql：");
        System.out.println(query.getDatabaseSql(DatabaseType.MYSQL));
        System.out.println();

        System.out.println("oracle：");
        System.out.println(query.getDatabaseSql(DatabaseType.ORACLE));
        System.out.println();

        System.out.println("pg：");
        System.out.println(query.getDatabaseSql(DatabaseType.POSTGRESQL));
        System.out.println();

        System.out.println("clickhouse：");
        System.out.println(query.getDatabaseSql(DatabaseType.CLICKHOUSE));
        System.out.println();

        System.out.println("sqlserver：");
        System.out.println(query.getDatabaseSql(DatabaseType.MSSQL));
        System.out.println();

        System.out.println("spark：");
        System.out.println(query.getDatabaseSql(DatabaseType.SPARK));
        System.out.println();
    }

    @Test
    public void testOneTable() {
        Conditions c1 = ModelFactory.getCpCondition(Cp.NONE, Op.LIKE,
                name, CustomSql.getCustomSql("'%s%'"));

        Conditions c2 = Conditions.getInstance(Cp.AND,
                ModelFactory.getCpCondition(Cp.NONE, Op.LIKE,
                        name, CustomSql.getCustomSql("'%s%'")),
                ModelFactory.getCpCondition(Cp.AND, Op.GREATER_THAN,
                        salary, CustomSql.getCustomSql("10")));

        Conditions first = ModelFactory.getCpCondition(Cp.NONE, Op.LIKE,
                name, CustomSql.getCustomSql("'%s%'"));

        Conditions second = Conditions.getInstance(Cp.OR,
                ModelFactory.getCpCondition(Cp.NONE, Op.LIKE,
                        name, CustomSql.getCustomSql("'z%'")),
                ModelFactory.getCpCondition(Cp.AND, Op.GREATER_THAN,
                        salary, CustomSql.getCustomSql("10"))
        );

        Conditions c3 = Conditions.getInstance(Cp.AND, first, second);

        Conditions where = Conditions.getInstance(Cp.NONE,
                c1, c2, c3);

        Function sum = Function.getFunction(FunctionType.SUM, salary);

        Conditions having = Conditions.getInstance(Cp.NONE,
                BinaryCondition.greaterThanOrEqual(sum, CustomSql.getCustomSql("10")));

        query.addColumn("", id).addColumn("", name).addColumn("total", sum)
                .addFrom(test)
                .setWHere(where)
                .addGroupBy(id).addGroupBy(name)
                .setHaving(having)
                .addOrderBy(SortType.DESC, id);
    }

    @Test
    public void testJoin() {
        Table joinTable = Table.getOriginalTable(schema, "test", "t1");
        Field id1 = Field.getField(joinTable, "id");
        Field name1 = Field.getField(joinTable, "name");
        Field salary1 = Field.getField(joinTable, "salary");

        Conditions c1 = Conditions.getInstance(Cp.NONE, BinaryCondition.equal(id1, id));
        Conditions c2 = Conditions.getInstance(Cp.AND,
                BinaryCondition.greaterThanOrEqual(salary1, CustomSql.getCustomSql("0")));
        Conditions joinConditions = Conditions.getInstance(Cp.NONE,
                c1, c2);
        Join join = Join.getJoin(JoinType.INNER, joinTable, joinConditions);

        query.addColumn(id, name, salary)
                .addColumn("id1", id1).addColumn("name1", name1).addColumn("salary1", salary1)
                .addFrom(test)
                .addJoin(join);
    }
}
