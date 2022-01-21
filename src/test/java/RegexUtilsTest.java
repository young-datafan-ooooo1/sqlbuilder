import com.sensesai.sql.util.RegexUtils;
import org.junit.Test;

/**
 * 正则表达式工具测试类.
 *
 * @author yinkaifeng
 * @since 2021-09-17 2:56 下午
 */
public class RegexUtilsTest {

    @Test
    public void testVarchar() {
        System.out.println(RegexUtils.isMatchVarchar("sdf"));
        System.out.println(RegexUtils.isMatchVarchar("'df'"));
        System.out.println(RegexUtils.isMatchVarchar("''"));
        System.out.println(RegexUtils.isMatchVarchar("'2021-01-01 12:01:01'"));
        System.out.println(RegexUtils.isMatchVarchar("'姓名：'"));
    }

    @Test
    public void testNumber(){
        System.out.println(RegexUtils.isMatchNumber("0"));
        System.out.println(RegexUtils.isMatchNumber("0.0000"));
        System.out.println(RegexUtils.isMatchNumber("-0.1"));
        System.out.println(RegexUtils.isMatchNumber("0.1"));
        System.out.println(RegexUtils.isMatchNumber("100.1200"));
        System.out.println(RegexUtils.isMatchNumber("1001.12"));
        System.out.println(RegexUtils.isMatchNumber("1."));
        System.out.println(RegexUtils.isMatchNumber("01.12"));
    }

    @Test
    public void testDatetimeVarchar() {
        System.out.println(RegexUtils.isMatchDateTimeVarchar("2021-01-01 00:00:00"));
        System.out.println(RegexUtils.isMatchDateTimeVarchar("'2021-01-01 00:00'"));
        System.out.println(RegexUtils.isMatchDateTimeVarchar("'2021-01-01 00:00:00'"));
        System.out.println(RegexUtils.isMatchDateTimeVarchar("'2021010100000 '"));
        System.out.println(RegexUtils.isMatchDateTimeVarchar("'20210101120101'"));
    }

    @Test
    public void testDateVarchar() {
        System.out.println(RegexUtils.isMatchDateVarchar("2021-01-01"));
        System.out.println(RegexUtils.isMatchDateVarchar("'2021-01'"));
        System.out.println(RegexUtils.isMatchDateVarchar("'2021-01-01'"));
        System.out.println(RegexUtils.isMatchDateVarchar("'20210101'"));
        System.out.println(RegexUtils.isMatchDateVarchar("'20210101120101'"));
    }

    @Test
    public void testTimeVarchar() {
        System.out.println(RegexUtils.isMatchTimeVarchar("'12:01:'"));
        System.out.println(RegexUtils.isMatchTimeVarchar("'12:01:01'"));
        System.out.println(RegexUtils.isMatchTimeVarchar("'120101'"));
        System.out.println(RegexUtils.isMatchTimeVarchar("'1201:01'"));
    }
}
