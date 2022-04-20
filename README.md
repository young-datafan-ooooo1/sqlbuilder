
# 项目介绍

### SQLBULIDER初衷
- 为了让SQL拼接更加的简单一些，通过一些结构定义，生成各种数据库支持的SQL。

### 说明

sqlbuilder

通用的sql构建包，目前支持（mysql、oracle、postgresql、sqlserver、Clickhouse）的大部分函数，以及多表关联。

帮助文档：http://www.young-datafan.com/docs-sqlbuilder/intro/



## 下载
https://github.com/young-datafan/sqlbuilder

## Maven
```xml
 <dependency>
      <groupId>com.young-datafan</groupId>
      <artifactId>sqlbuilder</artifactId>
      <version>1.4.2</version>
</dependency>
```

## Gradle via JCenter
``` groovy
compile 'com.young-datafan:sqlbuilder:1.4.2'
```

## 目前支持的函数

### 基础函数

| 函数名称      | 函数描述      | MYSQL | ORACLE | POSTGRESQL | CLICKHOUSE | KDW | SQLSERVER |
|-----------|-----------|-------|--------|------------|------------|-----|-----------|
| +         | +         | Y     | Y      | Y          | Y          | Y   | Y         |
| -         | -         | Y     | Y      | Y          | Y          | Y   | Y         |
| *         | *         | Y     | Y      | Y          | Y          | Y   | Y         |
| /         | /         | Y     | Y      | Y          | Y          | Y   | Y         |
| %         | %         | Y     | Y      | Y          | Y          | Y   | Y         |
| =         | =         | Y     | Y      | Y          | Y          | Y   | Y         |
| &lt;&gt;  | &lt;&gt;  | Y     | Y      | Y          | Y          | Y   | Y         |
| &lt;      | &lt;         | Y     | Y      | Y          | Y          | Y   | Y         |
| &gt;      | &gt;         | Y     | Y      | Y          | Y          | Y   | Y         |
| &lt;=     | &lt;=        | Y     | Y      | Y          | Y          | Y   | Y         |
| &gt;=     | &gt;=        | Y     | Y      | Y          | Y          | Y   | Y         |
| and       | and       | Y     | Y      | Y          | Y          | Y   | Y         |
| or        | or        | Y     | Y      | Y          | Y          | Y   | Y         |
| not       | not       | Y     | Y      | Y          | Y          | Y   | Y         |
| case when | case when | Y     | Y      | Y          | Y          | Y   | Y         |

### 数学函数

| 函数名称        | 函数描述           | MYSQL | ORACLE | POSTGRESQL | CLICKHOUSE | KDW | SQLSERVER |
|-------------|----------------|-------|--------|------------|------------|-----|-----------|
| abs(x)      | 绝对值            | Y     | Y      | Y          | Y          | Y   | Y         |
| round(x)    | 四舍五入           | Y     | Y      | Y          | Y          | Y   | Y         |
| ceil(x)     | 向上取整           | Y     | Y      | Y          | Y          | Y   | Y         |
| floor(x)    | 向下取整           | Y     | Y      | Y          | Y          | Y   | Y         |
| power(x, y) | 返回x的y次方        | Y     | Y      | Y          | Y          | Y   | Y         |
| e()         | 返回e的值          | Y     | Y      | Y          | Y          | Y   | Y         |
| pi()        | 返回pi的值         | Y     | Y      | Y          | Y          | Y   | Y         |
| exp(x)      | 返回e的x次方        | Y     | Y      | Y          | Y          | Y   | Y         |
| exp2(x)     | 返回2的x次方        | Y     | Y      | Y          | Y          | Y   | Y         |
| exp10(x)    | 返回10的x次方       | Y     | Y      | Y          | Y          | Y   | Y         |
| log(x)      | 返回log以e为底的对数值  | Y     | Y      | Y          | Y          | Y   | Y         |
| log2(x)     | 返回log以2为底的对数值  | Y     | Y      | Y          | Y          | Y   | Y         |
| log10(x)    | 返回log以10为底的对数值 | Y     | Y      | Y          | Y          | Y   | Y         |
| sqrt(x)     | 对x开平方          | Y     | Y      | Y          | Y          | Y   | Y         |
| cbrt(x)     | 对x开立方          | Y     | Y      | Y          | Y          | Y   | N         |

### 日期函数

| 函数名称                | 函数描述     | MYSQL | ORACLE | POSTGRESQL | CLICKHOUSE | KDW | SQLSERVER |
|----------------|-------------|-------|--------|------------|------------|-----|-----------|
| current_date      | 当前日期     | y     | y      | y          | y          | y   | y         |
| current_timestamp | 当前时间     | y     | y      | y          | y          | y   | y         |
| yesterday         | 昨天日期     | y     | y      | y          | y          | y   | y         |
| year_end            | 今年最后一天   | y     | y      | y          | y          | y   | y         |
| quarter_end         | 本季度最后一天  | y     | y      | y          | y          | y   | y         |
| month_end           | 本月最后一天   | y     | y      | y          | y          | y   | y         |
| week_end            | 本周最后一天   | y     | y      | y          | y          | y   | y         |
| year_begin          | 今年第一天    | y     | y      | y          | y          | y   | y         |
| quarter_begin       | 本季度第一天   | y     | y      | y          | y          | y   | y         |
| month_begin         | 本月第一天    | y     | y      | y          | y          | y   | y         |
| week_begin          | 本周第一天    | y     | y      | y          | y          | y   | y         |
| datediff            | 日期间隔计算   | y     | y      | y          | y          | y   | y         |
| adddatetime         | 日期加      | y     | y      | y          | y          | y   | y         |
| subdatetime         | 日期减      | y     | y      | y          | y          | y   | y         |
| toYear              | 取年份      | y     | y      | y          | y          | y   | y         |
| toQuarter           | 取季度数     | Y     | Y     | Y          | Y          | Y    | Y         |
| toMonth             | 取月份数     | Y     | Y     | Y          | Y          | Y    | Y         |
| toWeek              | 取周数       | Y     | Y     | Y          | Y          | Y    | Y        |
| toDayOfMonth        | 取月天数     | Y     | Y     | Y          | Y          | Y     | Y        |
| toDayOfWeek         | 取周天数     | Y     | Y     | Y          | Y           | Y    | Y        |
| toHour              | 取小时       | Y     | Y     | Y          | Y           | Y   | Y        |
| toMinute            | 取分钟数     | Y      | Y    | Y          | Y           | Y   | Y        |
| toSecond            | 取秒数       | Y     | Y    | Y          | Y           | Y    | Y        |
| formatDate_Y_M      | 取年月       | Y     | Y    | Y          | Y            | Y    | Y       |
| to_char             | 日期转字符串  | Y     | Y    | Y          | Y            | Y    | Y        |
| timestampToChar     | 时间转字符串  | Y     | Y    | Y          | Y            | Y    | Y       |
| to_date             | 字符串转日期  | Y     | Y    | Y          | Y            | Y    | Y       |
| toTimestamp         | 字符串转时间  | Y     | Y    | Y          | Y            | Y    | Y       |


### 字符串函数

| 函数名称    | 函数描述     | MYSQL | ORACLE | POSTGRESQL | CLICKHOUSE | KDW | SQLSERVER |
|---------|----------|-------|--------|------------|------------|-----|-----------|
| concat  | 字符串拼接    | Y     | Y      | Y          | Y          | Y   | Y         |
| instr   | 查找       | Y     | Y      | Y          | Y          | Y   | Y         |
| lower   | 转小写      | Y     | Y      | Y          | Y          | Y   | Y         |
| upper   | 转大写      | Y     | Y      | Y          | Y          | Y   | Y         |
| trim    | 去空（两边）   | Y     | Y      | Y          | Y          | Y   | Y         |
| trim    | 去空（从左）   | Y     | Y      | Y          | Y          | Y   | Y         |
| rtrim   | 去空（从右）   | Y     | Y      | Y          | Y          | Y   | Y         |
| lsubstr | 截取（从左）   | Y     | Y      | Y          | Y          | Y   | Y         |
| rsubstr | 截取（从右）   | Y     | Y      | Y          | Y          | Y   | Y         |
| substr  | 截取（自定）   | Y     | Y      | Y          | Y          | Y   | Y         |
| replace | 替换（按内容）  | Y     | Y      | Y          | Y          | Y   | Y         |
| length  | 计算字符串长度  | Y     | Y      | Y          | Y          | Y   | Y         |

### 判断函数

| 函数名称        | 函数描述  | MYSQL | ORACLE | POSTGRESQL | CLICKHOUSE | KDW | SQLSERVER |
|-------------|-------|-------|--------|------------|------------|-----|-----------|
| is null     | 是否为空  | Y     | Y      | Y          | Y          | Y   | Y         |
| is not null | 是否不为空 | Y     | Y      | Y          | Y          | Y   | Y         |

### 类型转换

| 函数名称          | 函数描述      | MYSQL | ORACLE | POSTGRESQL | CLICKHOUSE | KDW | SQLSERVER |
|---------------|-----------|-------|--------|------------|------------|-----|-----------|
| toDecimal64   | 转化为小数     | Y     | Y      | Y          | Y          | Y   | Y         |
| toUInt8OrZero | 字符串转化为整数  | Y     | Y      | Y          | Y          | Y   | Y         |
| toString      | 转化为字符串    | Y     | Y      | Y          | Y          | Y   | Y         |


### 聚合函数

| 函数名称           | 函数描述        | mysql | oracle | postgresql | clickhouse | kdw | sqlserver |
|----------------|-------------|-------|--------|------------|------------|-----|-----------|
| sum            | 求和          | Y     | Y      | Y          | Y          | Y   | Y         |
| count_any      | 统计数量（不含空值）  | Y     | Y      | Y          | Y          | Y   | Y         |
| count_distinct | 统计数量（去重）    | Y     | Y      | Y          | Y          | Y   | Y         |
| count          | 统计数量        | Y     | Y      | Y          | Y          | Y   | Y         |
| avg            | 求平均数        | Y     | Y      | Y          | Y          | Y   | Y         |
| max            | 求最大值        | Y     | Y      | Y          | Y          | Y   | Y         |
| min            | 求最小值        | Y     | Y      | Y          | Y          | Y   | Y         |
| median         | 中位数         | N     | Y      | Y          | Y          | Y   | N         |
| var_samp       | 方差          | Y     | Y      | Y          | Y          | Y   | Y         |
| stddev_samp    | 标准差         | Y     | Y      | Y          | Y          | Y   | Y         |
| weighted_mean  | 加权平均         | Y     | Y      | Y          | Y          | Y   | Y         |

