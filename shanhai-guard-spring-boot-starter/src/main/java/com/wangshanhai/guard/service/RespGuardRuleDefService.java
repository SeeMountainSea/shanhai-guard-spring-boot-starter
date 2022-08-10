package com.wangshanhai.guard.service;

/**
 * 使用自定义规则处理指定字段的数据
 */
public interface RespGuardRuleDefService {
    /**
     * 自定义处理字段方法
     * @param ruleId 规则ID
     * @param fieldValue 文件清单
     * @return
     */
    public Object jsonGenerator(String ruleId,Object fieldValue);
}
