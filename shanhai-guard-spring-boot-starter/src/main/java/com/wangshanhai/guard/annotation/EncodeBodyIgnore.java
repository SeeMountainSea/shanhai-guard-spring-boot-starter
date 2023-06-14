package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 自加密忽略注解
 * @author Shmily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncodeBodyIgnore {
}
