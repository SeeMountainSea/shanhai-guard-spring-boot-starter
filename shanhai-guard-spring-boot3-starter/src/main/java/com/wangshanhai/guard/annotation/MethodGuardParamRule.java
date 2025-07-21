package com.wangshanhai.guard.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 保护方法参数规则定义
 * @author Fly.Sky
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGuardParamRule {
    /**
     * 目标对象索引
     */
    int targetIndex() default 0;

    /**
     * 规则ID ref:targetType=2 时生效
     */
    String ruleId() default "";
    /**
     * 目标对象类型 1: DTO对象 2:根据规则
     */
    int targetType()  default 1;
}
