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
    Object encrypt(String original, MethodGuardField methodGuardField);

    /**
     * 执行字段解密
     * @param original 原始数值
     * @param methodGuardField 字段注解
     * @return
     */
    Object decrypt(String original, MethodGuardField methodGuardField);
}
