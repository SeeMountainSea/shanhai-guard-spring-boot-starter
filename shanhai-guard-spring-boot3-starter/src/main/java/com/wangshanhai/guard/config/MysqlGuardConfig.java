package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MYSQL 防护配置
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.mysqlguard")
public class MysqlGuardConfig {
    /**
     * 是否开启
     */
    private Boolean enable=false;
    /**
     * 要求包含where语句
     */
    private Boolean whereExist=false;
    /**
     * 要求包含where语句
     */
    private Boolean limitExist=false;
    /**
     * 每次查询数据的上限
     */
    private Integer queryLimit=20000;
}
