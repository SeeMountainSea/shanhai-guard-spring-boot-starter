package com.wangshanhai.guard.mybatis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 临时数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShanHaiTmpData implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 待处理数据
     */
    private String sourceValue;
    /**
     * 加密算法
     */
    private String encryptMethod;
    /**
     * 解密算法
     */
    private String decryptMethod;
    /**
     * 脱敏算法
     */
    private String hyposensitMethod;
    /**
     * 执行模式  执行模式  1：新增 2：更新 3:查询
     */
    private int execModel;
    /**
     * 规则ID，用于自行根据规则进行相关扩展
     */
    private String ruleId;
    /**
     * 目标字段
     */
    private String targetField;
    /**
     * 目标类
     */
    private String targetClass;
}