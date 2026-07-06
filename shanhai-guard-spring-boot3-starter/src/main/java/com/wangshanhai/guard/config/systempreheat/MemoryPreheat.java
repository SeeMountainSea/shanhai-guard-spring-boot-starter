package com.wangshanhai.guard.config.systempreheat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统资源预热组件
 * @author Fly.Sky
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryPreheat {
    /**
     * 触发压测的 Cron 表达式。配置为 "-" 则不启用
     **/
    private String startCron = "-";

    /**
     * 目标物理内存占用峰值 (MB)
     **/
    private long targetMb = 500;

    /**
     *每次步进拉升的内存大小 (MB)
     **/
    private long stepMb = 20;

    /**
     * 拉升期间隔时间 (毫秒)
     **/
    private long rampUpMs = 5000;

    /**
     *达到目标峰值后的保持时间 (毫秒)
     **/
    private long holdMs = 180000;

    /**
     * 安全阈值百分比 (针对系统当前剩余内存)
     * 例如 80 表示：每次申请内存前，确保系统剩余内存扣减本次申请量后，仍大于 系统总内存*20%。
     * 同时：单次申请量不得超过当前系统剩余内存的 80%。
     */
    private double maxSystemPercent = 80;
}
