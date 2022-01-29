package com.sayrmb.guard.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FileGuard {

    /**
     * 校验不通过提示信息
     */
    String message() default "";
    /**
     * 校验方式
     */
    GuardType type() default GuardType.SUFFIX;

    /**
     * 支持的文件后缀
     */
    String[] supportedSuffixes() default {};

    /**
     * 支持的文件类型
     * @return
     */
    FileType[] supportedFileTypes() default {};

    enum GuardType {
        /**
         * 仅校验后缀
         */
        SUFFIX,
        /**
         * 校验文件头
         */
        BYTES
    }
}