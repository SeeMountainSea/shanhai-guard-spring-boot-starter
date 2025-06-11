package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 启用数据防护
 * @author Shmily
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShanHaiDataGuard {

}