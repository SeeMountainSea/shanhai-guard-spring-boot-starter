package com.sayrmb.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL & xss注入检测配置
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "sayhi.webguard")
public class WebGuardConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 拦截范围
     */
    private List<String> pathPatterns=new ArrayList<>();
}
