package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 报文按照普通文本方式脱敏
 * @author Shmily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveTextBody {
}
