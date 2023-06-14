package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 自加密注解
 * @author Shmily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncodeBody {
    /**
     * 规则ID
     * @return
     */
    String ruleId() default "";
}
