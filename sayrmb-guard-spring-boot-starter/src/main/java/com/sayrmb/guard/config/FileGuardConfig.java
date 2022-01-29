package com.sayrmb.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件防火墙配置
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "sayhi.fileguard")
public class FileGuardConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 拦截范围
     */
    private List<String> pathPatterns=new ArrayList<>();
    /**
     * 不拦截范围
     */
    private List<String> excludePathPatterns=new ArrayList<>();
    /**
     * 文件类型白名单
     */
    private String suffix="jpg,gif,png,ico,bmp,jpeg";
    /**
     * 开启文件类型二进制校验
     */
    private Boolean fileRealCheck=false;
    /**
     * 是否开启上传日志
     */
    private Boolean logTarce=false;
}
