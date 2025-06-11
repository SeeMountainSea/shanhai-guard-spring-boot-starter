package com.wangshanhai.guard.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据审计配置
 *
 * @author Fly.Sky
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.dataaudit")
public class DataAuditConfig {
    /**
     * 忽略的列,多个表用分号分隔，例如：TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2; TABLE3.*; *.COLUMN1,COLUMN2
     * @apiNote
     *  TABLE_NAME1.COLUMN1,COLUMN2 : 表示忽略这个表的这2个字段
     *  TABLE3.*: 表示忽略这张表的INSERT/UPDATE，delete暂时还保留
     *  *.COLUMN1,COLUMN2:表示所有表的这个2个字段名都忽略
     */
    private   String ignoredTableColumns;
}
