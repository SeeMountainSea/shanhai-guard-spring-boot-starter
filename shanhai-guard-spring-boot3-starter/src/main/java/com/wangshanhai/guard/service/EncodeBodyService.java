package com.wangshanhai.guard.service;

import org.springframework.http.server.ServerHttpResponse;

/**
 * @author Shmily
 */
public interface EncodeBodyService {
    /**
     * 解析加密参数
     * @param body 加密报文
     * @return
     */
    default Object encodeRespBody(Object body, ServerHttpResponse response){
        return body;
    };

    /**
     * 按照自定义规则ID解析加密参数
     * @param ruleId 规则ID
     * @param body 加密报文
     * @return
     */
    default Object encodeRespBody(String ruleId,Object body,ServerHttpResponse response){
        return body;
    };

}
