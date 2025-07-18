package com.wangshanhai.guard.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShanHaiGuardException extends RuntimeException {
    private String code="500";
    private String message;

    public ShanHaiGuardException(String message) {
        this.message = message;
    }
}