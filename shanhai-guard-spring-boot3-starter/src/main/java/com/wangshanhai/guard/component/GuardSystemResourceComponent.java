package com.wangshanhai.guard.component;


import com.wangshanhai.guard.config.SystemPeakConfig;
import com.wangshanhai.guard.utils.CpuStressor;
import com.wangshanhai.guard.utils.MemoryStressor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 资源峰值模拟任务
 *
 * @author Fly.Sky
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "shanhai.systempeak",
        name = {"enable"},
        havingValue = "true"
)
@EnableScheduling
@EnableConfigurationProperties(SystemPeakConfig.class)
public class GuardSystemResourceComponent {
    @Autowired
    private SystemPeakConfig systemLoadTask;
    /**
     * 资源峰值模拟任务
     */
    @Scheduled(cron = "${shanhai.systempeak.runCron:}")
    public void loadCurrentSystemPeak() {
        // 默认参数：CPU压力30秒，内存70%，峰值保持60秒
        int cpuTime = systemLoadTask.getCpuTime();
        int memPercent = systemLoadTask.getMemPercent();
        int holdTime = systemLoadTask.getHoldTime();
        boolean operatingSystemMemoryCheck=systemLoadTask.isOperatingSystemMemoryCheck();
        int systemMemPercent = systemLoadTask.getOperatingSystemMemPercent();
        System.out.println("=====  资源峰值模拟开始 =====");
        System.out.printf(" 参数: CPU压力=%ds | 内存目标=%d%% | 峰值保持=%ds |物理内存极限:%d%%%n%n",
                cpuTime, memPercent, holdTime,systemMemPercent);

        CpuStressor cpuStressor = new CpuStressor();
        MemoryStressor memStressor = new MemoryStressor();

        try {
            // 阶段1: 触发资源峰值
            cpuStressor.startStress(cpuTime,systemLoadTask.getCpuSingleCoreTimes());
            memStressor.allocate(memPercent,operatingSystemMemoryCheck,systemMemPercent);

            // 阶段2: 保持峰值状态
            System.out.printf("%n[系统] 峰值状态保持 %d 秒...%n", holdTime);
            Thread.sleep(holdTime  * 1000L);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 阶段3: 资源释放（确保任何情况下都会执行）
            System.out.println("\n=====  释放资源 =====");
            cpuStressor.stopStress();
            memStressor.release();

            // 最终状态报告
            System.out.println("\n=====  模拟完成 =====");
            System.out.println(" 所有资源已释放，系统恢复正常状态");
        }
    }
}
