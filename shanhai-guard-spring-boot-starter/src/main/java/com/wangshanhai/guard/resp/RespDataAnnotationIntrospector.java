package com.wangshanhai.guard.resp;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.wangshanhai.guard.annotation.RespFieldGuard;
import com.wangshanhai.guard.config.RespGuardConfig;
import com.wangshanhai.guard.service.RespGuardRuleDefService;
import com.wangshanhai.guard.utils.Logger;

public class RespDataAnnotationIntrospector extends NopAnnotationIntrospector {
    private RespGuardConfig respGuardConfig;
    private RespGuardRuleDefService respGuardRuleDefService;
    public RespDataAnnotationIntrospector(RespGuardConfig respGuardConfig, RespGuardRuleDefService respGuardRuleDefService) {
        this.respGuardConfig = respGuardConfig;
        this.respGuardRuleDefService=respGuardRuleDefService;
    }

    @Override
    public Object findSerializer(Annotated annotated) {
        RespFieldGuard annotation = annotated.getAnnotation(RespFieldGuard.class);
        if (annotation != null) {
            if(respGuardConfig.isTraceLog()){
                Logger.info("[ShanhaiRespGuard]-info:{}",annotated.toString());
            }
            return new RespDataSerializer(null,respGuardRuleDefService,annotation.ruleId());
        }
        return null;
    }
}
