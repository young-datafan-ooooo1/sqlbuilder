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
    public void testVarchar(){
        System.out.println(RegexUtils.isMatchVarchar("sdf"));
        System.out.println(RegexUtils.isMatchVarchar("'df'"));
        System.out.println(RegexUtils.isMatchVarchar("''"));
    }
}
