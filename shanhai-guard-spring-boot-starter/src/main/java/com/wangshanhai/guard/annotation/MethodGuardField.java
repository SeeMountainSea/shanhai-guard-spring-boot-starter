package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 *  保护规则配置
 * @author Fly.Sky
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGuardField {
    /**
     * 参数保护规则
     * @return
     */
    String paramGuardRuleId() default "";

    /**
     * 结果保护规则
     * @return
     */
    String resultGuardRuleId() default "";
}
