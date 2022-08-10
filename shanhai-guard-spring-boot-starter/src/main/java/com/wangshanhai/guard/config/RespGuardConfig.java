package com.wangshanhai.guard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isTraceLog() {
        return traceLog;
    }

    public void setTraceLog(boolean traceLog) {
        this.traceLog = traceLog;
    }
}
