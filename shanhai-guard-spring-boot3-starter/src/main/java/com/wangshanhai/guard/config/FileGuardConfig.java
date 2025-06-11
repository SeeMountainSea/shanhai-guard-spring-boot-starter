package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件防火墙配置
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.fileguard")
public class FileGuardConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 是否开启ZIP扫描
     */
    private Boolean zipScan=false;
    /**
     * ZIP压缩包可信范围
     */
    private String zipSafeSuffixs="";
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
