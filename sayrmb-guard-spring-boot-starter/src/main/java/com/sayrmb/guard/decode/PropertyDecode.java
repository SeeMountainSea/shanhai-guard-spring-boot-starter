package com.sayrmb.guard.decode;

import org.springframework.lang.Nullable;

import java.util.Properties;

public  abstract class PropertyDecode {
    /**
     * 自定义解析算法
     * @param envProperties 通用配置参数
     * @param configProperties 待解密属性key:value
     * @return
     */
    @Nullable
    public abstract Properties getProperty(Properties envProperties,Properties configProperties);
}
