package com.wangshanhai.guard.utils;

/**
 * 统一异常编码
 * @author Fly.Sky
 */
public interface ShanHaiGuardErrorCode {
    /**
     * 方法解密错误
     */
    String METHOD_DECRYPT_ERROR = "210001";
    /**
     * 方法加密错误
     */
    String METHOD_ENCRYPT_ERROR = "210002";
    /**
     * 组件初始化错误
     */
    String COMPONENT_INIT_ERROR = "210003";
    /**
     * 文件上传告警
     */
    String FILE_UPLOAD_ALERT="210004";
    /**
     * 敏感词高级
     */
    String WORDS_SENSITIVE_LIMIT="210005";

    /**
     * 响应报文加密错误
     */
    String ENCODE_BODY_ERROR="210006";
    /**
     * 请求报文解密错误
     */
    String DECODE_BODY_ERROR="210007";
}
