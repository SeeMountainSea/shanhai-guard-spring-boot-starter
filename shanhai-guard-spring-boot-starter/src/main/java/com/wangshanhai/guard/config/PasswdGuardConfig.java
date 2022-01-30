package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 口令复杂度检测配置
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.passwdguard")
public class PasswdGuardConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 密码最小位数
     */
    private Integer minLength=8;
    /**
     * 密码最大位数
     */
    private Integer maxLength=16;
    /**
     * 包含大小写字母
     */
    private Boolean characterExist=false;
    /**
     * 包含数字
     */
    private Boolean numberExist=false;
    /**
     * 包含特殊符号
     */
    private Boolean symbolExist=false;
    /**
     * 不包含键盘顺序
     */
    private Boolean keyboardNotExist=false;
}
