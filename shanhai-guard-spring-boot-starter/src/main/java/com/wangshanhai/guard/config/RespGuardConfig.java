package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 序列化编码配置
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.respguard")
public class RespGuardConfig {
    /**
     * 是否启用组件
     */
    private boolean enable=false;
    /**
     * 启用跟踪日志
     */
    private boolean traceLog=false;

}
