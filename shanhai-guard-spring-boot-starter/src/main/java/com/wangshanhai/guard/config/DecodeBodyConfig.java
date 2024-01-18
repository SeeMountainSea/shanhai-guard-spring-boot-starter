package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义Body解码规则
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.decodebody")
public class DecodeBodyConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 解密模式（1: 全量 2:自定义 默认：1）
     */
    private Integer mode=1;
    /**
     * 是否启用敏感词过滤
     */
    private Boolean enableSensitive=false;
    /**
     * 敏感词过滤模式 （1:自动脱敏存储 2:拒绝执行）
     */
    private Integer sensitiveFilterMode=1;
}
