package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 数据防护
 */
@Inherited
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDataGuard {
    /**
     * 规则ID，用于自行根据规则进行相关扩展
     * @return
     */
    String ruleId() default "";
    /**
     * 是否启用数据加密(新增和更新均会调用)
     * @return
     */
    boolean encrypt() default false;
    /**
     * 加密算法
     * @return
     */
    String encryptMethod() default "";
    /**
     * 数据更新时不加密
     * @return
     */
    boolean denyUpdateEncrypt() default false;
    /**
     * 是否启用数据查询解密
     * @return
     */
    boolean decrypt() default false;

    /**
     * 解密算法
     * @return
     */
    String decryptMethod() default "";
    /**
     * 是否启用数据脱敏(新增和更新均会调用)
     * @return
     */
    boolean hyposensit() default false;

    /**
     * 数据脱敏算法
     * @return
     */
    String hyposensitMethod() default "idcard";
    /**
     * 数据更新时不脱敏
     * @return
     */
    boolean denyUpdateHyposensit() default false;
}