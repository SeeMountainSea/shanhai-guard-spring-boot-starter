package com.wangshanhai.guard.service;

import com.wangshanhai.guard.annotation.MethodGuardField;

/**
 * 自定义实现加解密逻辑
 * @author Fly.Sky
 */
public interface MethodFieldGuardService {
    /**
     * 执行字段加密
     * @param original 原始数值
     * @param methodGuardField 字段注解
     * @param fieldName 字段名
     * @return
     */
   default Object handleReq(Object original,String fieldName, MethodGuardField methodGuardField){return original;};
    /**
     * 执行对象加密（根据规则ID）
     * @param original 原始数值
     * @param ruleId  规则ID
     * @param fieldName 字段名
     * @return
     */
   default Object handleReq(Object original,String fieldName, String ruleId){return original;};
    /**
     * 执行字段解密
     * @param original 原始数值
     * @param methodGuardField 字段注解
     * @return
     */
    default Object handleResp(Object original, MethodGuardField methodGuardField){return original;};
}
