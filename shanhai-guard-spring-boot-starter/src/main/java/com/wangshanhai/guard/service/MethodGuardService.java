package com.wangshanhai.guard.service;

/**
 * 处理加解密服务
 * @author Fly.Sky
 */
public interface MethodGuardService {
    /**
     * 处理入参字段加密
     * @param arg
     * @return
     */
    Object encryptFieldsFromDto(Object arg);
    /**
     * 根据规则处理加解密
     * @param arg
     * @return
     */
    Object encryptFieldsFromRule(Object arg,String ruleId);
    /**
     * 处理结果集解密
     * @param result
     * @return
     */
    Object decryptFields(Object result);
}
