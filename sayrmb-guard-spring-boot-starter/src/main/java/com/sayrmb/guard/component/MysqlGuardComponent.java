package com.sayrmb.guard.component;

import com.sayrmb.guard.config.MysqlGuardConfig;
import com.sayrmb.guard.interceptor.MysqlSQLScanInterceptor;
import com.sayrmb.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MysqlGuardConfig.class)
@ConditionalOnProperty(
        prefix = "sayhi.mysqlguard",
        name = "enable",
        havingValue = "true")
public class MysqlGuardComponent {
    @Autowired
    private MysqlGuardConfig mysqlGuardConfig;

    @Bean
    public MysqlSQLScanInterceptor mysqlSQLScanInterceptor() {
        Logger.info("[MysqlSQLScan-Guard-Init]-init Component");
        return new MysqlSQLScanInterceptor(mysqlGuardConfig);
    }
}