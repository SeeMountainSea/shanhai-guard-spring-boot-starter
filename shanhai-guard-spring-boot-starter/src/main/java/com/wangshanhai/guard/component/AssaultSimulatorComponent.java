package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.AssaultSimulatorConfig;
import com.wangshanhai.guard.interceptor.AssaultSimulatorInterceptor;
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
 * 混沌模拟组件
 * @author Fly.Sky
 */
@Configuration
@EnableConfigurationProperties(AssaultSimulatorConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
@ConditionalOnProperty(
        prefix = "shanhai.assaultsimulator",
        name = "enable",
        havingValue = "true"
)
public class AssaultSimulatorComponent  implements WebMvcConfigurer {
    @Autowired
    private AssaultSimulatorConfig  assaultSimulatorConfig;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Logger.info("[Assault-Simulator-Init]-init Component");
        registry.addInterceptor(new AssaultSimulatorInterceptor(this.assaultSimulatorConfig)).addPathPatterns(assaultSimulatorConfig.getPathPatterns())
                .excludePathPatterns(assaultSimulatorConfig.getExcludePathPatterns());
    }
}
