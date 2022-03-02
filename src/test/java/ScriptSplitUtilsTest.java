import com.youngdatafan.sqlbuilder.util.ScriptSplitUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 脚本切割测试.
 *
 * @author yinkaifeng
 * @since 2022-01-19 9:57 上午
 */
public class ScriptSplitUtilsTest {

    @Test
    public void test() throws IOException {
        String dir = System.getProperty("user.dir");
        String filename = dir + "/src/test/resources/script/script.sql";
        File file = new File(filename);
        String script = FileUtils.readFileToString(file, Charset.forName("utf-8"));
        List<String> list = ScriptSplitUtils.split(script);
        System.out.println("size=" + list.size());
        for (String str : list) {
            System.out.println("===================");
            System.out.println(str);
            System.out.println("===================\n");
        }
    }
}
