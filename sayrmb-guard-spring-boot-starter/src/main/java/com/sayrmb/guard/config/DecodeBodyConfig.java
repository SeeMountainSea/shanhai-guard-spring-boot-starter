package com.sayrmb.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义Body解码规则
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "sayhi.decodebody")
public class DecodeBodyConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
}
