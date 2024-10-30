package com.wangshanhai.guard.config;

import com.wangshanhai.guard.dataplug.EncryptRule;
import com.wangshanhai.guard.dataplug.HyposensitRule;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 数据防护配置
 * @author Shmily
 */
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
     * 自定义正则脱敏规则
     */
    private List<HyposensitRule> hyposensitRulesExt;
    /**
     * 自定义加解密算法参数
     */
    private List<EncryptRule> encryptRulesExt;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<HyposensitRule> getHyposensitRulesExt() {
        return hyposensitRulesExt;
    }

    public void setHyposensitRulesExt(List<HyposensitRule> hyposensitRulesExt) {
        this.hyposensitRulesExt = hyposensitRulesExt;
    }

    public List<EncryptRule> getEncryptRulesExt() {
        return encryptRulesExt;
    }

    public void setEncryptRulesExt(List<EncryptRule> encryptRulesExt) {
        this.encryptRulesExt = encryptRulesExt;
    }

    public boolean isTraceLog() {
        return traceLog;
    }

    public void setTraceLog(boolean traceLog) {
        this.traceLog = traceLog;
    }

    public boolean isSlowFilter() {
        return slowFilter;
    }

    public void setSlowFilter(boolean slowFilter) {
        this.slowFilter = slowFilter;
    }
}
