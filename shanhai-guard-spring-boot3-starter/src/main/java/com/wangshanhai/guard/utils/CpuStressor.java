package com.wangshanhai.guard.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CPU压力生成器
 * @author Fly.Sky
 */
@Slf4j
public class CpuStressor {
    private ExecutorService executor;
    private final int coreCount = Runtime.getRuntime().availableProcessors();
    private volatile boolean running = false;

    /**
     * 启动CPU压力测试
     * @param durationSeconds 持续时间(秒)
     * @param cpuSingleCoreTimes     CPU密集型单核心计算次数（动态调整负载）
     */
    public void startStress(int durationSeconds,int cpuSingleCoreTimes,boolean operatingSystemCpuCheck,int systemCpuPercent) {
        if (running) {
            return;
        }
        running = true;
        if (operatingSystemCpuCheck&&isCpuPressureHigh(systemCpuPercent)) {
            return;
        }
        executor = Executors.newFixedThreadPool(coreCount  * 2);
        log.info("[CPU]  启动{}线程压力测试 (持续{}秒)", coreCount*2, durationSeconds);

        for (int i = 0; i < coreCount * 2; i++) {
            executor.submit(()  -> {
                long start = System.currentTimeMillis();
                // 计算密集型任务 - 莱布尼茨圆周率公式
                while (running && (System.currentTimeMillis()  - start) < durationSeconds * 1000L) {
                    double pi = 0;
                    for (int k = 0; k < cpuSingleCoreTimes; k++) { // 动态计算复杂度
                        pi += (k % 2 == 0 ? 1 : -1) / (2.0 * k + 1);
                    }
                }
            });
        }
    }
    private boolean isCpuPressureHigh(int systemCpuPercent) {
        // 检查系统内存使用率
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean unixBean) {
                double systemCpuLoad = unixBean.getCpuLoad()*100;
                if(systemCpuLoad>systemCpuPercent){
                    log.warn("[CPU物理检测] 已超越阈值 ➤ 当前负载:{}%,极限峰值:{}%",systemCpuLoad,systemCpuPercent);
                }else {
                    log.info("[CPU物理检测] 已使用 ➤ 当前负载:{}%,极限峰值:{}%",systemCpuLoad,systemCpuPercent);
                }
                // 当系统内存使用率超过85%时拒绝分配
                return systemCpuLoad > systemCpuPercent;
            }
        } catch (Exception e) {
            log.warn("[CPU物理检测]-{}", e.getMessage());
        }
        log.error("[CPU物理检测]-不支持物理CPU检测！");
        return false;
    }
    /**
     * 立即停止CPU压力
     **/
    public void stopStress() {
        running = false;
        if (executor != null) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(500,  TimeUnit.MILLISECONDS)) {
                    log.warn("[警告] CPU线程未及时终止");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("[CPU]  所有压力线程已停止");
        }
    }
}
