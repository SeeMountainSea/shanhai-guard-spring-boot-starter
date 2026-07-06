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
public class CpuPreheat {
    /**
     *触发压测的 Cron 表达式 (例如: "0 0 2 * * ?" 表示每天凌晨2点)。配置为 "-" 则不启用
     **/
    private String startCron = "-";

    /**
     * 目标 CPU 占用率峰值 (百分比，如 70 表示单核 70%)
     **/
    private double targetPercent = 70;

    /**
     * 每次步进拉升的幅度 (百分比，如 5 表示每次增加 5%)
     **/
    private double stepPercent = 5;

    /**
     * 拉升期间隔时间 (毫秒，如 3000 表示每 3 秒拉升一次)
     **/
    private long rampUpMs = 3000;

    /**
     * 达到目标峰值后的保持时间 (毫秒，如 120000 表示保持 2分钟)
     **/
    private long holdMs = 120000;

    /**
     * 消耗 CPU 的自旋线程数 (多核情况可适当增加)
     **/
    private int threads = 2;
}
