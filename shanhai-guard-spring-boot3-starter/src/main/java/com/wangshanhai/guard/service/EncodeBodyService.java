package com.wangshanhai.guard.service;

/**
 * @author Shmily
 */
public interface EncodeBodyService {
    /**
     * 解析加密参数
     * @param body 加密报文
     * @return
     */
    default String encodeRespBody(String body){
        return body;
    };

    /**
     * 按照自定义规则ID解析加密参数
     * @param ruleId 规则ID
     * @param body 加密报文
     * @return
     */
    default String encodeRespBody(String ruleId,String body){
        return ruleId+"@"+body;
    };
}
