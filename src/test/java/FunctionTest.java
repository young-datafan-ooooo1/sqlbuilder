import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.DateFormatType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.enums.TimeUnitType;
import com.sensesai.sql.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 函数测试.
 *
 * @author yinkaifeng
 * @since 2021-08-27 3:00 下午
 */
public class FunctionTest {
    private Schema schema;

    private Query query;

    private Table test;

    private Field id, salary, name, create_date, create_time, create_timestamp;

    @Before
    public void before() {
        schema = Schema.getSchema("");
        test = Table.getOriginalTable(schema, "test", "t");
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
        query.addFrom(test);
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

    /**
     * 测试算术函数(加、减、乘、除).
     */
    @Test
    public void testArithmetic() {
        CustomSql value = CustomSql.getCustomSql("10");
        Function add = Function.getFunction(FunctionType.ADD, salary, value);
        Function sub = Function.getFunction(FunctionType.SUB, salary, value);
        Function mul = Function.getFunction(FunctionType.MUL, salary, value);
        Function div = Function.getFunction(FunctionType.DIV, salary, value);

        query.addColumn("add" + "Val", add);
        query.addColumn("sub" + "Val", sub);
        query.addColumn("mul" + "Val", mul);
        query.addColumn("div" + "Val", div);
    }

    /**
     * 测试聚合函数.
     */
    @Test
    public void testAggregate() {
        Function count = Function.getFunction(FunctionType.COUNT, id);
        Function countDistinct = Function.getFunction(FunctionType.COUNT_DISTINCT, id);
        Function min = Function.getFunction(FunctionType.MIN, id);
        Function max = Function.getFunction(FunctionType.MAX, id);
        Function sum = Function.getFunction(FunctionType.SUM, id);
        Function avg = Function.getFunction(FunctionType.AVG, id);

        query.addColumn("countVal", count);
        query.addColumn("countDistinctVal", countDistinct);
        query.addColumn("minVal", min);
        query.addColumn("maxVal", max);
        query.addColumn("sumVal", sum);
        query.addColumn("avgVal", avg);
    }

    /**
     * 测试sign、rand、length、concat函数
     */
    @Test
    public void testSign() {
        Function sign = Function.getFunction(FunctionType.SIGN, salary);
        Function rand = Function.getFunction(FunctionType.RAND);
        Function len = Function.getFunction(FunctionType.LENGTH, name);
        Function concat = Function.getFunction(FunctionType.CONCAT, name, CustomSql.getCustomSql("'***'"));

        query.addColumn("sign" + "Val", sign);
        query.addColumn("rand" + "Val", rand);
        query.addColumn("len" + "Val", len);
        query.addColumn("concat" + "Val", concat);
    }

    /**
     * 测试upper、lower、trim、replace函数
     */
    @Test
    public void testString() {
        Function upper = Function.getFunction(FunctionType.UPPER, name);
        Function lower = Function.getFunction(FunctionType.LOWER, name);
        Function trim = Function.getFunction(FunctionType.TRIM, name);
        Function ltrim = Function.getFunction(FunctionType.LTRIM, name);
        Function rtrim = Function.getFunction(FunctionType.RTRIM, name);
        Function replace = Function.getFunction(FunctionType.REPLACE, name,
                CustomSql.getCustomSql("'s'"), CustomSql.getCustomSql("'*'"));

        query.addColumn("upper" + "Val", upper);
        query.addColumn("lower" + "Val", lower);
        query.addColumn("trim" + "Val", trim);
        query.addColumn("ltrim" + "Val", ltrim);
        query.addColumn("rtrim" + "Val", rtrim);
        query.addColumn("replace" + "Val", replace);
    }

    /**
     * 测试substring函数
     */
    @Test
    public void testSubstring() {
        Function substr = Function.getFunction(FunctionType.SUBSTRING,
                name, CustomSql.getCustomSql("1"), CustomSql.getCustomSql("3"));
        Function lsubstr = Function.getFunction(FunctionType.LEFT_SUBSTR,
                name, CustomSql.getCustomSql("2"));
        Function rsubstr = Function.getFunction(FunctionType.RIGHT_SUBSTR,
                name, CustomSql.getCustomSql("2"));

        query.addColumn("substr" + "Val", substr);
        query.addColumn("lsubstr" + "Val", lsubstr);
        query.addColumn("rsubstr" + "Val", rsubstr);
    }

    /**
     * 测试instr函数
     */
    @Test
    public void testInstr() {
        Function instr = Function.getFunction(FunctionType.INSTR, name, CustomSql.getCustomSql("'s'"));

        query.addColumn("instr" + "Val", instr);
    }

    /**
     * 测试排名函数
     */
    @Test
    public void testRank() {
        List<Model> partitionByList = new ArrayList<>();
        partitionByList.add(create_date);

        List<Model> orderByList = new ArrayList<>();
        orderByList.add(salary);
        orderByList.add(id);
        RankFunction rank = RankFunction.rank(partitionByList, orderByList);
        RankFunction dRank = RankFunction.denseRank(partitionByList, orderByList);
        RankFunction rNumber = RankFunction.rowNumber(partitionByList, orderByList);

        query.addColumn("rank" + "Val", rank);
        rank = RankFunction.rank(null, orderByList);
        query.addColumn("noParRankVal", rank);
        query.addColumn("dRank" + "Val", dRank);
        query.addColumn("rNumber" + "Val", rNumber);
    }

    /**
     * 测试日期格式化函数
     */
    @Test
    public void testDateFormat() {
        query.addColumn("a", Function.getFunction(FunctionType.DATE_TO_STRING,
                create_time, CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD_HH24_MI_SS.getCode())));

        query.addColumn("b", Function.getFunction(FunctionType.DATE_TO_STRING,
                create_time, CustomSql.getCustomSql(DateFormatType.YYYYMMDDHH24MISS.getCode())));

        query.addColumn("c", Function.getFunction(FunctionType.DATE_TO_STRING,
                create_time, CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD.getCode())));

        query.addColumn("d", Function.getFunction(FunctionType.DATE_TO_STRING,
                create_time, CustomSql.getCustomSql(DateFormatType.YYYYMMDD.getCode())));

        query.addColumn("e", Function.getFunction(FunctionType.DATE_TO_STRING,
                create_time, CustomSql.getCustomSql(DateFormatType.HH24_MI_SS.getCode())));

        query.addColumn("f", Function.getFunction(FunctionType.DATE_TO_STRING,
                create_time, CustomSql.getCustomSql(DateFormatType.HH24MISS.getCode())));
    }

    /**
     * 测试字符串转日期函数
     */
    @Test
    public void testStrToDate() {
        CustomSql datetime = CustomSql.getCustomSql("'2021-01-01 18:01:01'");
        CustomSql datetimeFormat = CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD_HH24_MI_SS.getCode());

        CustomSql datetime2 = CustomSql.getCustomSql("'20210101180101'");
        CustomSql datetimeFormat2 = CustomSql.getCustomSql(DateFormatType.YYYYMMDDHH24MISS.getCode());

        CustomSql date = CustomSql.getCustomSql("'2021-01-01'");
        CustomSql dateFormat = CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD.getCode());

        CustomSql date2 = CustomSql.getCustomSql("'20210101'");
        CustomSql dateFormat2 = CustomSql.getCustomSql(DateFormatType.YYYYMMDD.getCode());


        CustomSql time = CustomSql.getCustomSql("'18:01:01'");
        CustomSql timeFormat = CustomSql.getCustomSql(DateFormatType.HH24_MI_SS.getCode());

        CustomSql time2 = CustomSql.getCustomSql("'180101'");
        CustomSql timeFormat2 = CustomSql.getCustomSql(DateFormatType.HH24MISS.getCode());


        Function datetimeFunction = Function.getFunction(FunctionType.STRING_TO_DATE, datetime, datetimeFormat);
        Function dateFunction = Function.getFunction(FunctionType.STRING_TO_DATE, date, dateFormat);
        Function timeFunction = Function.getFunction(FunctionType.STRING_TO_DATE, time, timeFormat);

        Function datetimeFunction2 = Function.getFunction(FunctionType.STRING_TO_DATE, datetime2, datetimeFormat2);
        Function dateFunction2 = Function.getFunction(FunctionType.STRING_TO_DATE, date2, dateFormat2);
        Function timeFunction2 = Function.getFunction(FunctionType.STRING_TO_DATE, time2, timeFormat2);

        query.addColumn("valDateTime", datetimeFunction);
        query.addColumn("valDate", dateFunction);
        query.addColumn("valTime", timeFunction);

        query.addColumn("valDateTime2", datetimeFunction2);
        query.addColumn("valDate2", dateFunction2);
        query.addColumn("valTime2", timeFunction2);
    }


    /**
     * 测试日期加函数
     */
    @Test
    public void testAddDateTime() {
        CustomSql value = CustomSql.getCustomSql("1");

        query.addColumn("", create_time);
        query.addColumn("year", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.YEAR.getCode())));

        query.addColumn("quarter", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.QUARTER.getCode())));

        query.addColumn("month", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.MONTH.getCode())));

        query.addColumn("week", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.WEEK.getCode())));

        query.addColumn("day", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.DAY.getCode())));

        query.addColumn("hour", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.HOUR.getCode())));

        query.addColumn("minute", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.MINUTE.getCode())));

        query.addColumn("second", Function.getFunction(FunctionType.ADD_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.SECOND.getCode())));
    }

    /**
     * 测试日期减函数
     */
    @Test
    public void testSubDateTime() {
        CustomSql value = CustomSql.getCustomSql("1");

        query.addColumn("", create_time);

        query.addColumn("year", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.YEAR.getCode())));

        query.addColumn("quarter", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.QUARTER.getCode())));

        query.addColumn("month", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.MONTH.getCode())));

        query.addColumn("week", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.WEEK.getCode())));

        query.addColumn("day", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.DAY.getCode())));

        query.addColumn("hour", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.HOUR.getCode())));

        query.addColumn("minute", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.MINUTE.getCode())));

        query.addColumn("second", Function.getFunction(FunctionType.SUB_DATE_TIME,
                create_time, value, CustomSql.getCustomSql(TimeUnitType.SECOND.getCode())));
    }

    /**
     * 测试日间隔函数
     */
    @Test
    public void testDateDiff() {
        Function value = Function.getFunction(FunctionType.STRING_TO_DATE,
                CustomSql.getCustomSql("'2021-07-27 16:44:00'"),
                CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD.getCode()));
        query.addColumn("", create_time);
        query.addColumn("year", Function.getFunction(FunctionType.DATE_DIFF,
                CustomSql.getCustomSql(TimeUnitType.YEAR.getCode()),
                create_time, value));

        query.addColumn("quarter", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.QUARTER.getCode()),
                create_time, value));

        query.addColumn("month", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.MONTH.getCode()),
                create_time, value));

        query.addColumn("week", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.WEEK.getCode()),
                create_time, value));

        query.addColumn("day", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.DAY.getCode()),
                create_time, value));

        query.addColumn("hour", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.HOUR.getCode()),
                create_time, value));

        query.addColumn("minute", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.MINUTE.getCode()),
                create_time, value));

        query.addColumn("second", Function.getFunction(FunctionType.DATE_DIFF
                , CustomSql.getCustomSql(TimeUnitType.SECOND.getCode()),
                create_time, value));
    }

    /**
     * 测试power函数
     */
    @Test
    public void testPower() {
        Function power = Function.getFunction(FunctionType.POWER, id, CustomSql.getCustomSql("2"));

        query.addColumn("power" + "Val", power);
    }

    /**
     * 测试Percentage函数
     */
    @Test
    public void testPercentage() {
        Function percentage = Function.getFunction(FunctionType.PERCENTAGE, salary);

        query.addColumn("percentage" + "Val", percentage);
    }

    /**
     * 测试给定日期的年、季、月、周第一天和最后一天函数
     */
    @Test
    public void testBeginOrEndDate() {
        /*CustomSql datetime = CustomSql.getCustomSql("'2021-01-01 18:01:01'");
        CustomSql datetimeFormat = CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD_HH24_MI_SS.getCode());

        CustomSql date = CustomSql.getCustomSql("'2021-01-01'");
        CustomSql dateFormat = CustomSql.getCustomSql(DateFormatType.YYYY_MM_DD.getCode());

        Function dateFun = Function.getFunction(FunctionType.STRING_TO_DATE, date, dateFormat);*/
        Model dateFun = create_time;

        Function yearBegin = Function.getFunction(FunctionType.YEAR_BEGIN, dateFun);
        Function yearEnd = Function.getFunction(FunctionType.YEAR_END, dateFun);
        Function quarterBegin = Function.getFunction(FunctionType.QUARTER_BEGIN, dateFun);
        Function quarterEnd = Function.getFunction(FunctionType.QUARTER_END, dateFun);
        Function monthBegin = Function.getFunction(FunctionType.MONTH_BEGIN, dateFun);
        Function monthEnd = Function.getFunction(FunctionType.MONTH_END, dateFun);
        Function weekBegin = Function.getFunction(FunctionType.WEEK_BEGIN, dateFun);
        Function weekEnd = Function.getFunction(FunctionType.WEEK_END, dateFun);

        query.addColumn("dateFun", dateFun);
        query.addColumn("yearBegin", yearBegin);
        query.addColumn("yearEnd", yearEnd);
        query.addColumn("quarterBegin", quarterBegin);
        query.addColumn("quarterEnd", quarterEnd);
        query.addColumn("monthBegin", monthBegin);
        query.addColumn("monthEnd", monthEnd);
        query.addColumn("weekBegin", weekBegin);
        query.addColumn("weekEnd", weekEnd);
    }
}
