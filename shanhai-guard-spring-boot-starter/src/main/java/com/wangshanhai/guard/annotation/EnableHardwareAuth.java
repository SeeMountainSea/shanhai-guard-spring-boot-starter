package com.wangshanhai.guard.annotation;

import com.wangshanhai.guard.HardwareAuthImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用硬件授权
 * @author Shmily
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(HardwareAuthImportSelector.class)
public @interface EnableHardwareAuth {
}
