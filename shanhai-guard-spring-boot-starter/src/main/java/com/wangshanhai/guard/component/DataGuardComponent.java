package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.DataGuardConfig;
import com.wangshanhai.guard.mybatis.ShanHaiDataParameterInterceptor;
import com.wangshanhai.guard.mybatis.ShanHaiDataResultsInterceptor;
import com.wangshanhai.guard.service.ShanHaiDataGuardService;
import com.wangshanhai.guard.service.impl.DefaultDataGuardServiceImpl;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据加密&脱敏组件
 */
@Configuration
@ConditionalOnProperty(
        prefix = "shanhai.dataguard",
        name = "enable",
        havingValue = "true")
@EnableConfigurationProperties(DataGuardConfig.class)
public class DataGuardComponent {
    @Autowired
    private ShanHaiDataGuardService dataGuardService;
    @Autowired
    private DataGuardConfig shanhaiDataGuardConfig;
    @Bean
    public ShanHaiDataParameterInterceptor dataParameterInterceptor() {
        Logger.info("[DataGuard-ParameterInterceptor-Init]-init Component");
        return new ShanHaiDataParameterInterceptor(dataGuardService,shanhaiDataGuardConfig);
    }

    @Bean
    public ShanHaiDataResultsInterceptor dataResultsInterceptor() {
        Logger.info("[DataGuard-DataResultsInterceptor-Init]-init Component");
        return new ShanHaiDataResultsInterceptor(dataGuardService,shanhaiDataGuardConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public ShanHaiDataGuardService generateDefaultShanHaiDataGuardService() {
        return new DefaultDataGuardServiceImpl(shanhaiDataGuardConfig);
    };
}