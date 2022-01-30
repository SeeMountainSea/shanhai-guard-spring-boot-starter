package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 自解密忽略注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecodeBodyIgnore {
}
