package com.sayrmb.guard.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpBizException extends RuntimeException {
    private String code="500";
    private String message;

    public HttpBizException(String message) {
        this.message = message;
    }
}