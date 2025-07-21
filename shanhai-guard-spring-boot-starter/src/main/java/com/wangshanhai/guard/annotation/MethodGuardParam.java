package com.wangshanhai.guard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 保护方法参数
 * @author Fly.Sky
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGuardParam {
    /**
     * 加密规则
     */
    MethodGuardParamRule[] rules();
}
