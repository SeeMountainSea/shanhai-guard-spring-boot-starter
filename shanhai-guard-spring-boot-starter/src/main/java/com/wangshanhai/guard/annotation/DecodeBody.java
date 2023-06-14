package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 自解密注解
 * @author Shmily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecodeBody {
    /**
     * 规则ID
     * @return
     */
    String ruleId() default "";
}
