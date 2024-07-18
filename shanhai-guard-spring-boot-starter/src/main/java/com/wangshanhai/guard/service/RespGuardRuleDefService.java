package com.wangshanhai.guard.service;

/**
 * 使用自定义规则处理指定字段的数据
 * @author Shmily
 */
public interface RespGuardRuleDefService {
    /**
     * 自定义处理字段方法
     * @param ruleId 规则ID
     * @param fieldValue 字段值
     * @return
     */
    default Object jsonGenerator(String ruleId,Object fieldValue){
        return fieldValue;
    }
    /**
     * 自定义处理该类该字段的方法
     * @param tragetClass 目标类
     * @param tragetField 目标字段
     * @param fieldValue 字段值
     * @return
     */
    default Object jsonDynamicGenerator(String tragetClass,Object tragetField,Object fieldValue){
        return fieldValue;
    }
}
