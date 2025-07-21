package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.annotation.MethodGuardField;
import com.wangshanhai.guard.service.MethodFieldGuardService;
import org.springframework.stereotype.Service;

/**
 * 对象级加解密实现
 * @author Fly.Sky
 */
@Service
public class MethodFieldGuardServiceImpl implements MethodFieldGuardService {

    @Override
    public Object encrypt(Object original, MethodGuardField methodGuardField) {
        return null;
    }

    @Override
    public Object encrypt(Object original, String ruleId) {
        return null;
    }

    @Override
    public Object decrypt(Object original, MethodGuardField methodGuardField) {
        return null;
    }
}
