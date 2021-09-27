import com.sensesai.sql.parser.StellaSqlParser;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试.
 *
 * @author yinkaifeng
 * @since 2021-09-24 5:38 下午
 */
public class TableReplaceTest {
    private final SqlParser.Config config = SqlParser.configBuilder()
            .setLex(Lex.MYSQL)
            .build();

    private StellaSqlParser stellaSqlParser;

    private String sql;

    @Before
    public void before() {
        stellaSqlParser = new StellaSqlParser("test", "test_copy1");
    }

    @After
    public void after() throws SqlParseException {
        SqlParser parser = SqlParser.create(sql, config);
        SqlNode sqlNode = parser.parseQuery();
        System.out.println("源sql：");
        System.out.println(sql);

        String replace = stellaSqlParser.replace(sql);
        System.out.println("替换后的sql：");
        System.out.println(replace);
    }

    @Test
    public void test() {
        sql = "SELECT\n" +
                "\tt.id AS my_id,\n" +
                "\tt.`name`,\n" +
                "\tabs( t.salary ) AS salary,\n" +
                "\t( CASE t.id WHEN 1 THEN 'yes' ELSE 'no' END ) AS isFirst,\n" +
                "\t( CASE WHEN t.salary > 0 THEN 1 ELSE 0 END ) AS isPlus \n" +
                "FROM\n" +
                "\ttest t \n" +
                "ORDER BY\n" +
                "\tt.id DESC";
    }

    @Test
    public void test1() {
        sql = "SELECT\n" +
                "\ttest\n" +
                "\t.id,\n" +
                "\ttest.\n`name`,\n" +
                "\tabs( test.salary ) AS salary,\n" +
                "\t( CASE WHEN test.salary > 0 THEN 1 ELSE 0 END ) AS isPlus \n" +
                "FROM\n" +
                "\ttest";
    }

    @Test
    public void test2() {
        sql = "SELECT\ttest.id,\ttest.`name`,\tabs( test.salary ) AS salary,\t( CASE WHEN test.salary > 0 THEN 1 ELSE 0 END ) AS isPlus FROM\ttest\n";
    }

    @Test
    public void testOrderBy() {
        sql = "select id,`name`,test.salary from test order by test.id desc";
    }

    @Test
    public void testKeyword() {
        stellaSqlParser = new StellaSqlParser("INTERVAL", "test_copy1");
        sql = "select  date_add( t.create_date, INTERVAL 1 YEAR ) from `INTERVAL` t";
    }

    @Test
    public void testAliasEqualTable() {
        sql = "select test.id,test.`name`,test.create_date as test,test.test from test.test test";
    }

    @Test
    public void testSchema() {
        sql = "select t1.id,t1.`name`,t1.create_date from test.test t1";
    }

    @Test
    public void testJoin() {
        sql = "select t1.id,t2.`name`,t2.create_date from test t1 left join test t2 on t1.id=t2.id left join test t3 on t3.id=t2.id";
    }

    @Test
    public void testUnion(){
        /*sql = "select t.id,t.`name`,t.salary from test t\n" +
                "UNION ALL\n" +
                "select t1.id,t1.`name`,t1.salary from test t1";*/

        sql = "select t.id,t.`name`,t.salary from test t\n" +
                "UNION ALL\n" +
                "select test.id,test.`name`,test.salary from test";
    }

    @Test
    public void testAny(){
        sql = "SELECT\n" +
                " asset_diahfJUpWmJsgIRYaOVrRzHp,\n" +
                " asset_VvPHUrLVrkJU5CCKZinsOcVR,\n" +
                " asset_zNrkiOkEQxlPpVVYYXginzJh \n" +
                "FROM\n" +
                " (\n" +
                " SELECT\n" +
                "  ( t0.`test_id` + 2 ) AS `asset_diahfJUpWmJsgIRYaOVrRzHp`,(\n" +
                "   t0.`test_salary` + 2 \n" +
                "  ) AS `asset_VvPHUrLVrkJU5CCKZinsOcVR`,\n" +
                "  SUM( t0.`test_salary` ) AS `asset_zNrkiOkEQxlPpVVYYXginzJh` \n" +
                " FROM\n" +
                "  (\n" +
                "  SELECT\n" +
                "   * \n" +
                "  FROM\n" +
                "   ( SELECT t0.`id` AS `test_id`, t0.`name` AS `test_name`, t0.`salary` AS `test_salary` FROM `test` t0 ) p0\n" +
                "   LEFT JOIN (\n" +
                "   SELECT\n" +
                "    t1.`date` AS `test_user_date`,\n" +
                "    t1.`age` AS `test_user_age`,\n" +
                "    t1.`age1` AS `test_user_age1`,\n" +
                "    t1.`age2` AS `test_user_age2`,\n" +
                "    t1.`date1` AS `test_user_date1`,\n" +
                "    t1.`id` AS `test_user_id`,\n" +
                "    t1.`name` AS `test_user_name`,(\n" +
                "     t1.`age` + 2 \n" +
                "    ) AS `test_user_age_1`,\n" +
                "    CONCAT((( t1.`id` /( CASE WHEN t2.`id` IS NULL THEN 1 WHEN t2.`id` = 0 THEN 1 ELSE t2.`id` END ))* 100 ), '%' ) AS `test_user_tb` \n" +
                "   FROM\n" +
                "    `test_user` t1\n" +
                "    LEFT JOIN `test_user` t2 ON t1.`age` = t2.`age` \n" +
                "    AND t1.`age1` = t2.`age1` \n" +
                "    AND t1.`age2` = t2.`age2` \n" +
                "    AND t1.`id` = t2.`id` \n" +
                "    AND t1.`date` = date_add( t2.`date`, INTERVAL 1 YEAR ) \n" +
                "   ) p1 ON p0.`test_id` = p1.`test_user_id` \n" +
                "  ) t0 \n" +
                " GROUP BY\n" +
                "  ( t0.`test_id` + 2 ),(\n" +
                "   t0.`test_salary` + 2 \n" +
                "  ) \n" +
                " ) p0";
    }
}
