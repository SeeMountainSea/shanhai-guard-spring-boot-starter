package com.wangshanhai.guard.service;

public interface DecodeBodyService {
    /**
     * 解析加密参数
     * @param body
     * @return
     */
    public String decodeRequestBody(String body);
}
