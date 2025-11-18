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

    /**
     * 免脱敏权限编码
     * 登录用户拥有该权限，则可以绕开脱敏规则
     * @return
     */
    String superPermissionCode() default "";
}
