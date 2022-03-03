# 说明

sqlbuilder

通用的sql构建包，目前支持（mysql、oracle、postgresql、sqlserver、Clickhouse）的大部分函数，以及多表关联

--------------------------------------------------------------------------------
# 使用

## 通过构建对象Query构建一个查询语句，例如：
```
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
```
## 其他参考测试用例

## 替换sql中的表名，例如：
```
StellaSqlParser tableNameReplaceSqlParser = new StellaSqlParser("`", "test", "test_copy1");
String replace = tableNameReplaceSqlParser.replace(sql);
System.out.println("替换后的sql：");
System.out.println(replace);
```
