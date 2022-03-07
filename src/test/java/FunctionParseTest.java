import com.youngdatafan.sqlbuilder.dto.SqlField;
import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.model.CustomSql;
import com.youngdatafan.sqlbuilder.model.Model;
import com.youngdatafan.sqlbuilder.model.Query;
import com.youngdatafan.sqlbuilder.model.Schema;
import com.youngdatafan.sqlbuilder.model.Table;
import com.youngdatafan.sqlbuilder.util.ExpressionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FunctionParseTest {
    private Model model;

    private Map<String, Table> tableMap = new HashMap<>();

    private List<SqlField> sqlFields;

    @Before
    public void init() {
        Schema schema = Schema.getSchema("");
        Table table = Table.getOriginalTable(schema, "test", "t");
        tableMap.put("t", table);
        tableMap.put("", table);

        sqlFields = new ArrayList<>();
        sqlFields.add(new SqlField("", "id", "number"));
        sqlFields.add(new SqlField("", "name", "string"));
        sqlFields.add(new SqlField("", "salary", "number"));
        sqlFields.add(new SqlField("", "create_date", "date"));
        sqlFields.add(new SqlField("", "create_time", "datetime"));
    }

    @After
    public void printSql() {
        Table table = tableMap.get("t");
        Query query = new Query();
        if (model != null) {
            query.addColumn("field", model);
        } else {
            query.addColumn("", CustomSql.getCustomSql("*"));
        }
        query.addFrom(table);
        System.out.println(query.getDatabaseSql(DatabaseType.MYSQL));
        System.out.println();
        /*System.out.println(query.getDatabaseSql(DatabaseType.ORACLE));
        System.out.println();
        System.out.println(query.getDatabaseSql(DatabaseType.POSTGRESQL));
        System.out.println();
        System.out.println(query.getDatabaseSql(DatabaseType.CLICKHOUSE));
        System.out.println();
        System.out.println(query.getDatabaseSql(DatabaseType.MSSQL));
        System.out.println();*/
    }

    @Test
    public void functionTest() {
        List<String> list = new ArrayList<>();
        list.add("not(id >0)");
        list.add("e()");
        list.add("exp(2)");
        list.add("exp2(3)");
        list.add("exp10(name)");
        list.add("log(1)");
        list.add("log2(2)");
        list.add("log10(10)");
        list.add("sqrt(4)");
        list.add("cbrt(9)");
        list.add("to_int(id)");
        list.add("to_decimal(salary, 2)");
        list.add("year(create_date)");
        list.add("quarter(create_date)");
        list.add("month(create_date)");
        list.add("weekday(create_date)");
        list.add("to_day(create_date)");
        list.add("to_hour(create_time)");
        list.add("to_minute(create_time)");
        list.add("to_second(create_time)");
        list.add("year_month(create_date)");
        list.add("weighted_mean(id, salary)");
        boolean pass;
        for (int i = 0; i < list.size(); i++) {
            try {
                ExpressionUtils.verify(list.get(i), tableMap, sqlFields);
                pass = true;
            } catch (Exception e) {
                //e.printStackTrace();
                pass = false;
            }
            System.out.println("表达式：" + list.get(i) + "，验证结果：" + pass);
        }
    }
}
