package com.youngdatafan.sqlbuilder.util;

import com.alibaba.fastjson.JSONObject;
import com.youngdatafan.sqlbuilder.exception.SQLBuildException;
import com.youngdatafan.sqlbuilder.function.FunctionCheckDefinition;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 函数参数校验工具类.
 *
 * @author yinkaifeng
 * @since 2021-09-16 3:25 下午
 */
public class FunCheckUtils {

    private static final Map<String, FunctionCheckDefinition> CACHE = new HashMap<>();

    static {
        String filename = "function_param_check.json";
        InputStream is = null;
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            is = resource.getInputStream();
            String jsonStr = IOUtils.toString(is, "utf-8");
            List<FunctionCheckDefinition> dataList = JSONObject.parseArray(jsonStr, FunctionCheckDefinition.class);
            if (dataList != null && dataList.size() > 0) {
                for (FunctionCheckDefinition data : dataList) {
                    CACHE.put(data.getName().toLowerCase(), data);
                }
            }
        } catch (Exception e) {
            throw new SQLBuildException("加载函数参数校验配置文件失败", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 根据函数名称获取函数校验定义类.
     *
     * @param name 函数名称
     * @return 函数校验定义类
     */
    public static FunctionCheckDefinition getCheckDefinitionByName(String name) {
        return CACHE.get(name.toLowerCase());
    }

    /**
     * 获取函数对应下标参数允许的数据类型.
     *
     * @param name     函数名
     * @param index    下标
     * @param realSize 实际参数个数
     * @return 允许的数据类型
     */
    public static String[] getDataType(String name, int index, int realSize) {
        FunctionCheckDefinition checkDefinition = getCheckDefinitionByName(name);
        String[] checkRegexType = null;
        if (checkDefinition != null) {
            int max = checkDefinition.getMax();
            int min = checkDefinition.getMin();
            if (realSize < min || realSize > max) {
                if (min == max) {
                    throw new SQLBuildException("函数" + name + "参数个数不正确，需要：" + min + "，实际：" + realSize);
                } else {
                    throw new SQLBuildException("函数" + name + "参数个数不正确，最少：" + min + "，最多：" + max + "，实际：" + realSize);
                }
            }
            List<FunctionCheckDefinition.CheckDetail> params = checkDefinition.getParams();
            if (params != null && params.size() > 0) {
                int size = params.size();
                if (size > index) {
                    checkRegexType = params.get(index).getCheckRegexType();
                } else {
                    checkRegexType = params.get(size - 1).getCheckRegexType();
                }
            }
        }
        return checkRegexType == null ? new String[0] : checkRegexType;
    }
}
