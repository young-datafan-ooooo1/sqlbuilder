package com.sensesai.sql.model;

import com.sensesai.sql.enums.DatabaseType;
import com.sensesai.sql.enums.FunctionType;
import com.sensesai.sql.enums.ModelType;
import com.sensesai.sql.util.FunctionUtils;

/**
 * 函数模型.
 * 不同数据库参数和关键字都可能不一样，故分为两种做法，第一种是通过配置文件配置，这种适合固定参数个数的函数，
 * 另外一种，通过代码实现，这种适合可变参数。
 * 具体思路：
 * 1、通过配置文件配置：这种是否可以通过占位符号替换？
 * 2、代码实现：考虑到不同数据库有些适合在配置文件中配置，有些适合在代码中实现，故考虑通过函数名获取对应的类方法？
 * 函数可分为几种：
 * 1、固定参数个数，例如：LOWER('a')
 * 2、范围参数个数，例如：round(salary)、round(salary,2)
 * 3、不定参数个数，例如：1+1+1+1...、concat(name,'a','a','a','a')
 * 4、两组参数，并且都是可变的，例如：RANK ( ) OVER ( partition BY t.id asc ORDER BY t.salary desc )
 * 5、对于格式化等函数，不同数据库格式不一致，故可能还需要转换成对于数据库识别的格式
 * 6、函数中带distinct关键字，例如：count(distinct salary)
 *
 * @author yinkaifeng
 * @since 2021-08-26 4:42 下午
 */
public abstract class AbstractFunction implements Model {
    /**
     * 获取函数类型.
     *
     * @return 函数类型枚举
     */
    public abstract FunctionType getType();

    @Override
    public ModelType getModelType() {
        return ModelType.FUNCTION;
    }

    @Override
    public String getDatabaseSql(DatabaseType databaseType) {
        return FunctionUtils.toDatabaseSql(databaseType, this);
    }
}
