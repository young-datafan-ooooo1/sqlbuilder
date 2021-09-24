import com.sensesai.sql.enums.Cp;
import com.sensesai.sql.enums.DataType;
import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.enums.JoinType;
import com.sensesai.sql.enums.SortType;
import com.sensesai.sql.model.BinaryCondition;
import com.sensesai.sql.model.Conditions;
import com.sensesai.sql.model.Constant;
import com.sensesai.sql.model.CustomSql;
import com.sensesai.sql.model.Field;
import com.sensesai.sql.model.Function;
import com.sensesai.sql.model.Join;
import com.sensesai.sql.model.Query;
import com.sensesai.sql.model.Schema;
import com.sensesai.sql.model.Table;
import com.sensesai.sql.vo.PrecompileParameterVo;
import org.junit.Test;

import java.util.List;

/**
 * 例子.
 *
 * @author yinkaifeng
 * @since 2021-09-24 4:13 下午
 */
public class Example {

    @Test
    public void test() {

        //构建Schema对象
        Schema schema = Schema.getSchema("test");
        //构建表
        Table test = Table.getOriginalTable(schema, "test", "t");

        //构建字段
        Field id = Field.getField(test, "id");
        Field name = Field.getField(test, "name");

        //构建函数
        Function function = Function.getFunction(FunctionType.ABS, id);

        //关联
        //构建关联表
        Table joinTable = Table.getOriginalTable(schema, "test", "t1");
        Field id1 = Field.getField(joinTable, "id");
        //关联条件
        Conditions joinCondition = Conditions.getInstance(Cp.NONE, BinaryCondition.equal(id, id1));
        Join join = Join.getJoin(JoinType.LEFT, joinTable, joinCondition);

        //构建查询条件
        Conditions where = Conditions.getInstance();
        where.add(Conditions.getInstance(Cp.NONE,
                BinaryCondition.like(name,
                        CustomSql.getCustomSql("'%s%'"))));
        where.add(Conditions.getInstance(Cp.AND,
                BinaryCondition.greaterThanOrEqual(id,
                        Constant.getConstant(DataType.INT, "1", "", 10, 0))));

        //构建查询对象
        Query query = new Query();
        query.addColumn(id);
        query.addColumn("fields", function);
        query.addFrom(test);
        query.addJoin(join);
        query.setWHere(where);
        query.addOrderBy(SortType.DESC, id);
        //获取可执行sql
        String sql = query.getExecuteSql(DatabaseType.MYSQL);
        //获取预编译sql
        String preSql = query.getPreSql(DatabaseType.MYSQL);
        //获取预编译sql参数
        List<PrecompileParameterVo> precompileParameterVoList = query.getPrecompileParameterVoList();
        System.out.println("可执行sql:");
        System.out.println(sql);
        System.out.println("预编译sql:");
        System.out.println(preSql);
    }
}
