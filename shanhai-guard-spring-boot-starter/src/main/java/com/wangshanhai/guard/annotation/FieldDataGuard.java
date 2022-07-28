package com.wangshanhai.guard.annotation;

import com.wangshanhai.guard.dataplug.DataEncryptDef;
import com.wangshanhai.guard.dataplug.DataExecModel;
import com.wangshanhai.guard.dataplug.DataHyposensitDef;

import java.lang.annotation.*;

/**
 * 数据防护
 * @author Shmily
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
    String encryptMethod() default DataEncryptDef.SM3;
    /**
     * 执行加密算法的时机
     * @return
     */
    int encryptExecModel() default DataExecModel.SAVEANDUPDATE;
    /**
     * 是否启用数据查询解密
     * @return
     */
    boolean decrypt() default false;

    /**
     * 解密算法
     * @return
     */
    String decryptMethod() default DataEncryptDef.SM4;
    /**
     * 执行解密算法的时机
     * @return
     */
    int decryptExecModel() default DataExecModel.QUERY;
    /**
     * 是否启用数据脱敏(新增和更新均会调用)
     * @return
     */
    boolean hyposensit() default false;

    /**
     * 数据脱敏算法
     * @return
     */
    String hyposensitMethod() default DataHyposensitDef.RealName;
    /**
     * 执行脱敏算法的时机
     * @return
     */
    int hyposensitExecModel() default DataExecModel.QUERY;
}