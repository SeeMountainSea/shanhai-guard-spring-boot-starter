package com.wangshanhai.guard.annotation;

import java.lang.annotation.*;

/**
 * 全量数据动态控制
 * @author Shmily
 */
@Inherited
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RespDataGuard {

}
