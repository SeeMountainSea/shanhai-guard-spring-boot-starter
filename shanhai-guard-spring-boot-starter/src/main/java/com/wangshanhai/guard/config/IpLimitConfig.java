package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件防火墙配置
 * @author gaurd
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RefreshScope
@ConfigurationProperties(prefix = "shanhai.iplimit")
public class IpLimitConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 渠道IP白名单配置
     * key: 渠道标识, value: IP段列表
     */
    private Map<String, String[]> channels;
    /**
     * 拦截范围
     */
    private List<String> pathPatterns=new ArrayList<>();
    /**
     * 不拦截范围
     */
    private List<String> excludePathPatterns=new ArrayList<>();
}
