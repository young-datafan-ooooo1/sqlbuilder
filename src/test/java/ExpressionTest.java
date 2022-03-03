import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youngdatafan.sqlbuilder.dto.SqlField;
import com.youngdatafan.sqlbuilder.enums.DataType;
import com.youngdatafan.sqlbuilder.enums.DatabaseType;
import com.youngdatafan.sqlbuilder.model.Model;
import com.youngdatafan.sqlbuilder.model.Query;
import com.youngdatafan.sqlbuilder.model.Table;
import com.youngdatafan.sqlbuilder.util.ExpressionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表达式测试.
 *
 * @author yinkaifeng
 * @since 2021-11-09 5:22 下午
 */
public class ExpressionTest {

    @Test
    public void test(){
        String expressStr = "case when age>=20 and age<30 then '20-30' when age>=30 and age<40 then '30-40' when age>=40 and age<50 then '40-50' when age>=50 and age<60 then '50-60' else '其他' end";
        Table table = Table.getOriginalTable(null,"test","t");
        Map<String, Table> tableMap = new HashMap<>();
        tableMap.put("", table);
        Query query = new Query();
        Model model = ExpressionUtils.toModel(expressStr, tableMap);
        query.addColumn("", model);
        System.out.println(query.getExecuteSql(DatabaseType.MYSQL));
    }

    @Test
    public void testVerifyAndGetDataType(){
        String json = "{\"databaseType\":\"\",\"expression\":\"concat('2021-01-01','yyyy-MM-dd')\",\"fieldList\":[{\"fieldName\":\"CUSTOMER_ID\",\"fieldType\":\"Integer\",\"tableAlias\":\"\"},{\"fieldName\":\"FIRST_NAME\",\"fieldType\":\"String\",\"tableAlias\":\"\"},{\"fieldName\":\"LAST_NAME\",\"fieldType\":\"String\",\"tableAlias\":\"\"},{\"fieldName\":\"DOB\",\"fieldType\":\"Timestamp\",\"tableAlias\":\"\"},{\"fieldName\":\"PHONE\",\"fieldType\":\"String\",\"tableAlias\":\"\"}]}";
        JSONObject object = JSON.parseObject(json);
        String expressStr = object.getString("expression");
        Map<String, Table> tableMap = null;
        List<SqlField> fieldList = JSON.parseArray(object.getString("fieldList"),SqlField.class);
        DataType dataType = ExpressionUtils.verifyAndGetDataType(expressStr, tableMap, fieldList);
        Assert.assertNotNull(dataType);
    }
}
