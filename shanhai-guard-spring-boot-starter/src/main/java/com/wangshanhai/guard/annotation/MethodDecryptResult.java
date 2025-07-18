package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 解密方法结果
 * @author Fly.Sky
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodDecryptResult {
}
