package com.wangshanhai.guard.service.impl;

import com.wangshanhai.guard.annotation.MethodGuardField;
import com.wangshanhai.guard.service.MethodFieldGuardService;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;

/**
 * 默认实现
 * @author Fly.Sky
 */
public class DefaultMethodFieldGuardService implements MethodFieldGuardService {
    @Override
    public Object handleReq(Object original,String fieldName, MethodGuardField methodGuardField) {
        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.COMPONENT_INIT_ERROR,"请先实现自定义加密规则后再启用该组件!");
    }

    @Override
    public Object handleReq(Object original,String fieldName, String ruleId) {
        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.COMPONENT_INIT_ERROR,"请先实现自定义加密规则后再启用该组件!");
    }

    @Override
    public Object handleResp(Object original, MethodGuardField methodGuardField) {
        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.COMPONENT_INIT_ERROR,"请先实现自定义解密规则后再启用该组件!");
    }
}
