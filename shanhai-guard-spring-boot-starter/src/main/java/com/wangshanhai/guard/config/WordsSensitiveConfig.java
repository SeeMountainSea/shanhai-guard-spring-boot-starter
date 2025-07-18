package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 敏感词检测配置
 *
 * @author Fly.Sky
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.sensitivewords")
public class WordsSensitiveConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 响应报文脱敏
     */
    private Boolean enableResp=false;
    /**
     * 请求参数拦截范围
     */
    private List<String> reqPathPatterns=new ArrayList<>();
    /**
     * 响应参数拦截范围
     */
    private List<String> respPathPatterns=new ArrayList<>();
    /**
     * 敏感词过滤模式 （1:自动脱敏放行 2:拒绝执行）
     */
    private Integer sensitiveFilterMode=1;
    /**
     * 敏感词清单（多个以英文,分隔）
     */
    private String sensitiveWords;
    /**
     * 是否启用定时更新敏感词词库任务
     */
    private Boolean taskEnable=false;
    /**
     * 定时更新频率 （单位:秒，默认值：600s）
     */
    private Long taskIntevalPeriod=600L;

}
