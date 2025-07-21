package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 保护结果参数
 * @author Fly.Sky
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodGuardResult {
}
