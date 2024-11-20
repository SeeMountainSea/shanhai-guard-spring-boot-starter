package com.wangshanhai.guard.config;

import com.wangshanhai.guard.dataplug.EncryptRule;
import com.wangshanhai.guard.dataplug.HyposensitRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 数据防护配置
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.dataguard")
public class DataGuardConfig {
    /**
     * 是否启用组件
     */
    private boolean enable=false;
    /**
     * 启用跟踪日志
     */
    private boolean traceLog=false;
    /**
     * 启用性能监控
     */
    private boolean slowFilter=false;
    /**
     * 性能预警最小值（单位：ms）
     */
    private int slowTime=1000;
    /**
     * 自定义正则脱敏规则
     */
    private List<HyposensitRule> hyposensitRulesExt;
    /**
     * 自定义加解密算法参数
     */
    private List<EncryptRule> encryptRulesExt;
}
