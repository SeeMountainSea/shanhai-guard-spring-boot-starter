package com.wangshanhai.guard.service.impl;

import com.wangshanhai.guard.annotation.MethodGuardField;
import com.wangshanhai.guard.service.MethodFieldGuardService;
import com.wangshanhai.guard.service.MethodGuardService;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;

import java.util.Arrays;

/**
 * 处理加解密服务
 *
 * @author Fly.Sky
 */
public class MethodGuardServiceImpl implements MethodGuardService {
    MethodFieldGuardService methodFieldGuardService;

    public MethodGuardServiceImpl(MethodFieldGuardService methodFieldGuardService) {
        this.methodFieldGuardService = methodFieldGuardService;
    }

    @Override
    public Object encryptFieldsFromDto(Object arg) {
        if (arg == null) {
            return null;
        }
        // 反射处理对象字段加密
        Arrays.stream(arg.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(MethodGuardField.class))
                .forEach(field -> {
                    try {
                        MethodGuardField methodGuardField = field.getAnnotation(MethodGuardField.class);
                        field.setAccessible(true);
                        field.set(arg, methodFieldGuardService.encrypt(field.get(arg), methodGuardField));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.METHOD_ENCRYPT_ERROR,"方法级参数加密失败");
                    }
                });
        return arg;
    }

    @Override
    public Object encryptFieldsFromRule(Object arg, String ruleId) {
        return methodFieldGuardService.encrypt(arg,ruleId);
    }

    @Override
    public Object decryptFields(Object arg) {
        if (arg == null) {
            return null;
        }
        // 反射处理对象字段解密
        Arrays.stream(arg.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(MethodGuardField.class))
                .forEach(field -> {
                    try {
                        MethodGuardField methodGuardField = field.getAnnotation(MethodGuardField.class);
                        field.setAccessible(true);
                        String original = (String) field.get(arg);
                        field.set(arg, methodFieldGuardService.decrypt(original, methodGuardField));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.METHOD_DECRYPT_ERROR,"方法级参数解密失败");
                    }
                });
        return arg;
    }
}
