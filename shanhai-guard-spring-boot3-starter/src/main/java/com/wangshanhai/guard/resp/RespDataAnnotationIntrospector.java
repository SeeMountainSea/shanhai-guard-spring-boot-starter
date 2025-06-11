package com.wangshanhai.guard.resp;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.wangshanhai.guard.annotation.RespDataGuard;
import com.wangshanhai.guard.annotation.RespFieldGuard;
import com.wangshanhai.guard.config.RespGuardConfig;
import com.wangshanhai.guard.service.RespGuardRuleDefService;

import java.lang.reflect.Method;

/**
 * @author Shmily
 */
public class RespDataAnnotationIntrospector extends NopAnnotationIntrospector {
    private RespGuardConfig respGuardConfig;
    private RespGuardRuleDefService respGuardRuleDefService;
    public RespDataAnnotationIntrospector(RespGuardConfig respGuardConfig, RespGuardRuleDefService respGuardRuleDefService) {
        this.respGuardConfig = respGuardConfig;
        this.respGuardRuleDefService=respGuardRuleDefService;
    }

    @Override
    public Object findSerializer(Annotated annotated) {
        Class<?> target=null;
        if(annotated instanceof  AnnotatedMethod){
            try{
                AnnotatedMethod annotatedMethod = (AnnotatedMethod) annotated;
                Method method =annotatedMethod.getMember();
                target= method.getDeclaringClass();
                RespDataGuard respDataGuard=target.getAnnotation(RespDataGuard.class);
                if(respDataGuard!=null){
                    return new RespDataDynamicSerializer(null,respGuardRuleDefService,target.getName(),method.getName(),respGuardConfig);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        RespFieldGuard annotation = annotated.getAnnotation(RespFieldGuard.class);
        if (annotation != null) {
            return new RespDataSerializer(null,respGuardRuleDefService,annotation.ruleId(),target.getName(),respGuardConfig);
        }
        return null;
    }
}
