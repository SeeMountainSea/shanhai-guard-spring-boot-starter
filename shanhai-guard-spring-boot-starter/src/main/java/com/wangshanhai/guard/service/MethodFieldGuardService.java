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
     * @return
     */
    Object encrypt(Object original, MethodGuardField methodGuardField);
    /**
     * 执行对象加密（根据规则ID）
     * @param original 原始数值
     * @param ruleId  规则ID
     * @return
     */
    Object encrypt(Object original, String ruleId);
    /**
     * 执行字段解密
     * @param original 原始数值
     * @param methodGuardField 字段注解
     * @return
     */
    Object decrypt(Object original, MethodGuardField methodGuardField);
}
