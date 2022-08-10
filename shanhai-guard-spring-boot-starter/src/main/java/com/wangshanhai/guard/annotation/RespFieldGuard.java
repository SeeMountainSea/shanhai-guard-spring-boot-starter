package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 数据处理
 * @author Shmily
 */
@Inherited
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RespFieldGuard {
    /**
     * 规则ID，用于自行根据规则进行相关扩展
     * @return
     */
    String ruleId() default "";
}
