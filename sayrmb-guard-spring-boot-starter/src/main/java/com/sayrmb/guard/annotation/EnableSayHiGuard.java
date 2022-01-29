package com.sayrmb.guard.annotation;

import com.sayrmb.guard.GuardImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Guard
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GuardImportSelector.class)
public @interface EnableSayHiGuard {
}
