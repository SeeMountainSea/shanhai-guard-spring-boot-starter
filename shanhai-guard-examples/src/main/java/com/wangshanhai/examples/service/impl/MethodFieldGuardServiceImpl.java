package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.annotation.MethodGuardField;
import com.wangshanhai.guard.service.MethodFieldGuardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 对象级加解密实现
 * @author Fly.Sky
 */
@Slf4j
@Service
public class MethodFieldGuardServiceImpl implements MethodFieldGuardService {

    @Override
    public Object handleReq(Object original,String fieldName, MethodGuardField methodGuardField) {
        log.warn("[MethodFieldGuardService-encrypt]-source:{},fieldName:{},methodGuardField:{}",original,fieldName,methodGuardField);
        return original+"@MethodGuardField";
    }

    @Override
    public Object handleReq(Object original,String fieldName, String ruleId) {
        log.warn("[MethodFieldGuardService-encrypt]-source:{},fieldName:{},ruleId:{}",original,fieldName,ruleId);
        return original+"@encrypt";
    }

    @Override
    public Object handleResp(Object original, MethodGuardField methodGuardField) {
        log.warn("[MethodFieldGuardService-decrypt]-source:{},methodGuardField:{}",original,methodGuardField);
        return original+"@decrypt";
    }
}
