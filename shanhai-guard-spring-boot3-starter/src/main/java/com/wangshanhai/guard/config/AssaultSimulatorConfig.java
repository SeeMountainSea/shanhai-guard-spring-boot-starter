package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 混沌模拟器配置
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.assaultsimulator")
public class AssaultSimulatorConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 是否模拟请求缓慢
     */
    private Boolean enableSimulatorSleep=false;
    /**
     * 请求延时最小时长（单位:毫秒）
     */
    private Integer sleepTimeMin=500;
    /**
     * 请求延时最大时长（单位:毫秒）
     */
    private Integer sleepTimeMax=3000;
    /**
     * 请求延时区间阈值
     */
    private Integer sleepBatchSize=100;
    /**
     * 区间阈值内请求延时最小出现次数
     */
    private Integer sleepMinTimes=10;
    /**
     * 请区间阈值内求延时最大出现次数
     */
    private Integer sleepMaxTimes=20;

    /**
     * 是否模拟请求异常
     */
    private Boolean enableSimulatorException=false;
    /**
     * 请求延时区间阈值
     */
    private Integer exceptionBatchSize=100;
    /**
     * 区间阈值内请求延时最小出现次数
     */
    private Integer exceptionMinTimes=10;
    /**
     * 请区间阈值内求延时最大出现次数
     */
    private Integer exceptionMaxTimes=20;
    /**
     * 拦截范围
     */
    private List<String> pathPatterns=new ArrayList<>();
    /**
     * 不拦截范围
     */
    private List<String> excludePathPatterns=new ArrayList<>();
}
