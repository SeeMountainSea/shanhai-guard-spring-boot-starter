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
     * CPU密集型单核心计算次数（动态调整负载）
     */
    private int cpuSingleCoreTimes=800_000;
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
    /**
     * 操作系统级内存安全检测
     */
    private boolean operatingSystemMemoryCheck=false;
    /**
     * 操作系统级内存检测阈值
     */
    private int operatingSystemMemPercent=85;
    /**
     * 操作系统级CPU安全检测
     */
    private boolean operatingSystemCpuCheck=false;
    /**
     * 操作系统级CPU检测阈值
     */
    private int operatingSystemCpuPercent=85;
}
