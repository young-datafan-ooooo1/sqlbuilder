package com.youngdatafan.sqlbuilder.function.impl;

import com.youngdatafan.sqlbuilder.enums.DateFormatType;
import com.youngdatafan.sqlbuilder.enums.TimeUnitType;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.function.IDatabaseFunction;
import com.youngdatafan.sqlbuilder.model.AbstractFunction;
import com.youngdatafan.sqlbuilder.model.Function;
import com.youngdatafan.sqlbuilder.util.PropertyUtils;

import java.util.Objects;

/**
 * java实现数据库函数顶级父类，主要封装一些公共方法.
 *
 * @author yinkaifeng
 * @since 2021-09-03 10:37 上午
 */
public abstract class AbstractJavaFunction implements IDatabaseFunction {

    /**
     * 转function函数模型.
     *
     * @param abstractFunction 函数模型
     * @return function函数模型
     */
    protected Function isFunction(AbstractFunction abstractFunction) {
        if (!(abstractFunction instanceof Function)) {
            throw new SQLBuildException("函数构造方法不正确");
        }
        return (Function) abstractFunction;
    }

    /**
     * 校验参数个数.
     *
     * @param function 函数
     * @param size     需要的参数个数
     */
    protected void verifyParamSize(Function function, int size) {
        int realParamSize = function.getParamList().size();
        if (realParamSize != size) {
            throw new SQLBuildException("函数" + function.getFunctionType().getCode()
                    + "参数个数不正确，需要" + size + "个，实际" + realParamSize + "个");
        }
    }

    /**
     * 获取数据库对应的时间单位字符串.
     *
     * @param function  函数模型
     * @param paramSize 数据库函数需要的参数个数
     * @param index     时间单位参数所在位置下标
     * @return 数据库对应的时间单位字符串
     */
    protected String getDatabaseTimeUnit(Function function, int paramSize, int index) {
        TimeUnitType timeUnitType = getTimeUnitType(function, paramSize, index);
        return PropertyUtils.getDatabaseTimeUnit(getDatabaseType(), timeUnitType);
    }

    /**
     * 校验并根据传的参数获取时间单位枚举.
     *
     * @param function 函数定义.
     * @return 时间单位枚举
     */
    protected TimeUnitType getTimeUnitType(Function function, int paramSize, int index) {
        verifyParamSize(function, paramSize);
        String str = function.getParamList().get(index).getDatabaseSql(getDatabaseType());
        TimeUnitType type = TimeUnitType.getEnumByCode(str.replaceAll("'", ""));
        if (Objects.isNull(type)) {
            throw new SQLBuildException(getDatabaseType().getCode() + "数据库暂不支持" + str + "时间单位");
        }
        return type;
    }

    /**
     * 校验并根据传的参数获取时间格式枚举.
     *
     * @param function 函数定义.
     * @return 时间格式枚举
     */
    protected DateFormatType getDateFormatType(Function function, int paramSize, int index) {
        verifyParamSize(function, paramSize);
        String str = function.getParamList().get(index).getDatabaseSql(getDatabaseType());
        DateFormatType type = DateFormatType.getEnumByCode(str.replaceAll("'", ""));
        if (Objects.isNull(type)) {
            throw new SQLBuildException(getDatabaseType().getCode() + "数据库暂不支持" + str + "时间格式");
        }
        return type;
    }
}
