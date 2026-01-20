package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.IpLimitConfig;
import com.wangshanhai.guard.interceptor.IpLimitInterceptor;
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
 * IpLimitComponent
 * @author Fly.Sky
 */
@Configuration
@EnableConfigurationProperties(IpLimitConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
@ConditionalOnProperty(
        prefix = "shanhai.iplimit",
        name = "enable",
        havingValue = "true"
)
public class IpLimitComponent implements WebMvcConfigurer {
    @Autowired
    private IpLimitConfig  ipLimitConfig;
    @Autowired
    private IpLimitInterceptor ipLimitInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Logger.info("[IP-Limit-Init]-init Component");
        registry.addInterceptor(ipLimitInterceptor).addPathPatterns(ipLimitConfig.getPathPatterns())
                .excludePathPatterns(ipLimitConfig.getExcludePathPatterns());
    }
}
