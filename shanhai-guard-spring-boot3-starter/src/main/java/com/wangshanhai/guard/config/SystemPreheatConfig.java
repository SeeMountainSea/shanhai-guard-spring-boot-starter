package com.wangshanhai.guard.config;

import com.wangshanhai.guard.config.systempreheat.CpuPreheat;
import com.wangshanhai.guard.config.systempreheat.MemoryPreheat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 系统资源预热组件
 * @author Fly.Sky
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RefreshScope
@ConfigurationProperties(prefix = "shanhai.systempreheat")
public class SystemPreheatConfig {
    /**
     * CPU压测相关配置
     **/
    private CpuPreheat cpuPreheat;

    /**
     *内存压测相关配置
     **/
    private MemoryPreheat memoryPreheat;
}
