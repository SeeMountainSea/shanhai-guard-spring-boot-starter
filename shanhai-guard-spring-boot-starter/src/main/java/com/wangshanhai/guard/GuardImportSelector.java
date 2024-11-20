package com.wangshanhai.guard;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 自定义注解扫描的自动配置组件
 * @author Shmily
 */
public class GuardImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                "com.wangshanhai.guard.component.FileGuardComponent",
                "com.wangshanhai.guard.component.PasswdGuardComponent",
                "com.wangshanhai.guard.component.WebGuardComponent",
                "com.wangshanhai.guard.component.MysqlGuardComponent",
                "com.wangshanhai.guard.component.DataGuardComponent",
                "com.wangshanhai.guard.component.RespDataGuardComponent",
                "com.wangshanhai.guard.component.EncodeBodyComponent",
                "com.wangshanhai.guard.component.DecodeBodyComponent",
                "com.wangshanhai.guard.component.SensitiveWordsComponent",
                "com.wangshanhai.guard.component.DataAuditComponent"
        };
    }
}
