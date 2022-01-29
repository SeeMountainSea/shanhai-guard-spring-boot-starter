package com.sayrmb.guard.component;

import com.sayrmb.guard.service.PasswdService;
import com.sayrmb.guard.config.PasswdGuardConfig;
import com.sayrmb.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 密码检测组件
 */
@Configuration
@EnableConfigurationProperties(PasswdGuardConfig.class)
@ConditionalOnProperty(
        prefix = "sayhi.passwdguard",
        name = "enable",
        havingValue = "true"
)
public class PasswdGuardComponent {
    @Autowired
    private PasswdGuardConfig passwdGuardConfig;

    @Bean
    public PasswdService passwdService(){
        Logger.info("[Passwd-Guard-Init]-init Component");
        return new PasswdService(passwdGuardConfig);
    }
}
