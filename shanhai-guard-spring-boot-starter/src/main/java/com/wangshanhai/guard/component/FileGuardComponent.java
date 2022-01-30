package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.FileGuardConfig;
import com.wangshanhai.guard.interceptor.FileScanInterceptor;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件上传检测组件
 */
@Configuration
@EnableConfigurationProperties(FileGuardConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
@ConditionalOnProperty(
        prefix = "shanhai.fileguard",
        name = "enable",
        havingValue = "true"
)
public class FileGuardComponent  implements WebMvcConfigurer {
    @Autowired
    private FileGuardConfig fileGuardConfig;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Logger.info("[File-Guard-Init]-init Component");
        registry.addInterceptor(new FileScanInterceptor(this.fileGuardConfig)).addPathPatterns(fileGuardConfig.getPathPatterns())
        .excludePathPatterns(fileGuardConfig.getExcludePathPatterns());
    }
}
