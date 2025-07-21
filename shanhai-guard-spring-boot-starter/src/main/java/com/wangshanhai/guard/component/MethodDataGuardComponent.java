package com.wangshanhai.guard.component;

import com.wangshanhai.guard.annotation.MethodEncryptParam;
import com.wangshanhai.guard.annotation.MethodEncryptRule;
import com.wangshanhai.guard.annotation.MethodGuardField;
import com.wangshanhai.guard.service.MethodFieldGuardService;
import com.wangshanhai.guard.service.impl.DefaultMethodFieldGuardService;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
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
import java.util.Arrays;
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
                            args[i] = this.encryptFieldsFromDto(args[i]);
                            break;
                        case 2:
                            args[i] = this.encryptFieldsFromRule(args[i],parameters[i].getName(),methodEncryptRule.ruleId());
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
                    .map(this::decryptFields)
                    .collect(Collectors.toList());
        }
        return decryptFields(result);
    }

    @Bean
    @ConditionalOnMissingBean
    public MethodFieldGuardService generateDefaultMethodFieldGuardService() {
        return new DefaultMethodFieldGuardService();
    };

    private Object encryptFieldsFromDto(Object arg) {
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
                        field.set(arg, methodFieldGuardService.encrypt(field.get(arg),field.getName(), methodGuardField));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.METHOD_ENCRYPT_ERROR,"方法级参数加密失败");
                    }
                });
        return arg;
    }

    private Object encryptFieldsFromRule(Object arg,String fieldName, String ruleId) {
        return methodFieldGuardService.encrypt(arg,fieldName,ruleId);
    }

    private Object decryptFields(Object arg) {
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
