package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 资源峰值模拟配置
 * @author Fly.Sky
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.systempeak")
public class SystemPeakConfig {
    /**
     * 是否启用
     */
    private String enabled="false";
    /**
     * CPU压力时间，单位秒
     */
    private int cpuTime = 30;
    /**
     * 内存目标，单位百分比
     */
    private int memPercent = 70;
    /**
     * 峰值保持时间，单位秒
     */
    private int holdTime = 60;
    /**
     * 负载提升执行时间表达式
     */
    private String runCron="0 0/5 * * * ?";
}
