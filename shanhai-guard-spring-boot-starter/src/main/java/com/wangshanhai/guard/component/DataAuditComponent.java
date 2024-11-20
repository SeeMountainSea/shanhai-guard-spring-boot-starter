package com.wangshanhai.guard.component;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.wangshanhai.guard.config.DataAuditConfig;
import com.wangshanhai.guard.mybatis.audit.ShanHaiDataAuditInterceptor;
import com.wangshanhai.guard.service.ShanHaiDataAuditService;
import com.wangshanhai.guard.service.impl.DefaultDataAuditServiceImpl;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据审计组件
 * @author Shmily
 */
@Configuration
@ConditionalOnProperty(
        prefix = "shanhai.dataaudit",
        name = "enable",
        havingValue = "true")
@EnableConfigurationProperties(DataAuditConfig.class)
public class DataAuditComponent {
    @Autowired
    private ShanHaiDataAuditService dataAuditService;
    @Autowired
    private DataAuditConfig dataAuditConfig;
    @Bean
    public ShanHaiDataAuditInterceptor dataAuditInterceptor() {
        Logger.info("[DataGuard-DataAuditInterceptor-Init]-init Component");
        return new ShanHaiDataAuditInterceptor(dataAuditService,dataAuditConfig);
    }
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(dataAuditInterceptor());
        return interceptor;
    }
    @Bean
    @ConditionalOnMissingBean
    public DefaultDataAuditServiceImpl generateDefaultDataAuditServiceImpl() {
        return new DefaultDataAuditServiceImpl();
    };
}