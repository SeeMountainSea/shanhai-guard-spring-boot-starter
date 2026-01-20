package com.wangshanhai.guard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IP限制注解
 * @author guard
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IpLimit {
    /**
     * 消息头名称数组（按优先级顺序）
     */
    String[] headers() default {};

    /**
     * 渠道标识
     */
    String channel() default "";

    /**
     * 是否启用IP限制
     */
    boolean enabled() default true;
}