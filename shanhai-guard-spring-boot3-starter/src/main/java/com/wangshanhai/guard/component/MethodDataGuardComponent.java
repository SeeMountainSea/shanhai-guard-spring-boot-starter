package com.wangshanhai.guard.component;

import com.wangshanhai.guard.annotation.MethodEncryptParam;
import com.wangshanhai.guard.annotation.MethodEncryptRule;
import com.wangshanhai.guard.service.MethodFieldGuardService;
import com.wangshanhai.guard.service.MethodGuardService;
import com.wangshanhai.guard.service.impl.DefaultMethodFieldGuardService;
import com.wangshanhai.guard.service.impl.MethodGuardServiceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方法级数据防护组件
 * @author Fly.Sky
 */
@Aspect
@Configuration
@ConditionalOnProperty(
        prefix = "shanhai.methoddataguard",
        name = "enable",
        havingValue = "true")
public class MethodDataGuardComponent {

    @Autowired
    private MethodGuardService methodGuardService;
    @Autowired(required = false)
    private MethodFieldGuardService methodFieldGuardService;
    @Pointcut("@annotation(com.wangshanhai.guard.annotation.MethodEncryptParam)")
    public void pointCutEncryptParam() {

    }

    /**
     * 入参加密切点
     */
    @Around("pointCutEncryptParam()")
    public Object encryptParam(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        MethodEncryptParam encryptParam =method.getAnnotation(MethodEncryptParam.class);
        Parameter[] parameters = method.getParameters();
        for(int j=0;j<encryptParam.rules().length;j++){
            MethodEncryptRule methodEncryptRule=encryptParam.rules()[j];
            for(int i=0; i<parameters.length; i++){
                if(methodEncryptRule.targetIndex()==i){
                    switch (methodEncryptRule.targetType()){
                        case 1:
                            args[i] = methodGuardService.encryptFieldsFromDto(args[i]);
                            break;
                        case 2:
                            args[i] = methodGuardService.encryptFieldsFromRule(args[i],methodEncryptRule.ruleId());
                            break;
                    }
                }
            }
        }
        return point.proceed(args);
    }

    /**
     * 出参解密切点
     */
    @AfterReturning(pointcut="@annotation(com.wangshanhai.guard.annotation.MethodDecryptResult)", returning="result")
    public Object decryptResult(JoinPoint point, Object result) {
        if(result instanceof List){
            return ((List<?>)result).stream()
                    .map(methodGuardService::decryptFields)
                    .collect(Collectors.toList());
        }
        return methodGuardService.decryptFields(result);
    }


    @Bean
    @ConditionalOnMissingBean
    public MethodGuardService generateMethodGuardService() {
        return new MethodGuardServiceImpl(methodFieldGuardService);
    };

    @Bean
    @ConditionalOnMissingBean
    public MethodFieldGuardService generateDefaultMethodFieldGuardService() {
        return new DefaultMethodFieldGuardService();
    };
}
