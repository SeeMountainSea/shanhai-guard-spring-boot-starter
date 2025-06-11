package com.wangshanhai.guard.annotation;

import com.wangshanhai.guard.GuardImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用山海Guard
 * @author Shmily
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GuardImportSelector.class)
public @interface EnableShanHaiGuard {
}
