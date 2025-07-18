package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 *  加密字段配置
 * @author Fly.Sky
 */
@Inherited
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGuardField {
    /**
     * 加密字段规则
     * @return
     */
    String encryptRuleId() default "";

    /**
     * 解密字段规则
     * @return
     */
    String decryptRuleId() default "";
}
