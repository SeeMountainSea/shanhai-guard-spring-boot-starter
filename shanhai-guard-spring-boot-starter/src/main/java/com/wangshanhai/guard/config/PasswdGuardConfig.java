package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 口令复杂度检测配置
 * @author Shmily
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
     * 密码最小位数(默认值：8)
     */
    private Integer minLength=8;
    /**
     * 密码最大位数(默认值：16)
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
    /**
     * 不包含相同字符
     */
    private Boolean allSameNotExist=false;
    /**
     * 相同字符连续个数(默认值：4)
     */
    private Integer allSameNum=4;
    /**
     * 不包含连续字符
     */
    private Boolean seqSameNotExist=false;
    /**
     * 连续字符连续个数(默认值：4)
     */
    private Integer seqSameNum=4;
}
