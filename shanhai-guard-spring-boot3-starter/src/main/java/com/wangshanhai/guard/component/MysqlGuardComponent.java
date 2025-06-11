package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.MysqlGuardConfig;
import com.wangshanhai.guard.interceptor.MysqlSQLScanInterceptor;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据防护
 * @author Shmily
 */
@Configuration
@EnableConfigurationProperties(MysqlGuardConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.mysqlguard",
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