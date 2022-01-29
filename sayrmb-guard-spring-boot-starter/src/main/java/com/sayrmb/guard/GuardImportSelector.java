package com.sayrmb.guard;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 自定义注解扫描的自动配置组件
 */
public class GuardImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                "com.sayrmb.guard.component.FileGuardComponent",
                "com.sayrmb.guard.component.PasswdGuardComponent",
                "com.sayrmb.guard.component.WebGuardComponent",
                "com.sayrmb.guard.component.MysqlGuardComponent",
                "com.sayrmb.guard.component.DecodeBodyComponent"
        };
    }
}
