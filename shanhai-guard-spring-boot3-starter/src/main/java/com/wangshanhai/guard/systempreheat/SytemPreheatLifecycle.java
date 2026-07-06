package com.wangshanhai.guard.systempreheat;

import com.wangshanhai.guard.config.SystemPreheatConfig;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import com.wangshanhai.guard.utils.SystemLoadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import sun.misc.Unsafe;

import jakarta.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 系统资源预热组件(执行器)
 *
 * @author Fly.Sky
 */
@Slf4j
public class SytemPreheatLifecycle {
    private final SystemPreheatConfig properties;
    private final SystemPreheatCronScheduler chaosCronScheduler;

    private volatile double currentCpuPercent = 0.0;
    private volatile long currentMemoryMB = 0;
    private final AtomicBoolean cpuRunning = new AtomicBoolean(false);
    private final AtomicBoolean memRunning = new AtomicBoolean(false);

    private final ExecutorService lifeCyclePool = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "Shanhai-SystemPreheat-State-Machine");
        t.setDaemon(true);
        return t;
    });

    private ExecutorService cpuBurnerPool;
    private final List<Thread> activeCpuThreads = new ArrayList<>();

    private Unsafe unsafe;
    private final List<Long> memoryPointers = new ArrayList<>();
    private final Object memLock = new Object();

    public SytemPreheatLifecycle(SystemPreheatConfig properties, SystemPreheatCronScheduler scheduler) {
        this.properties = properties;
        this.chaosCronScheduler = scheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            this.unsafe = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new ShanHaiGuardException(ShanHaiGuardErrorCode.SYTEM_PREHEAT_UNSAFE,"初始化 Unsafe 失败，请检查 JVM 参数: --add-opens java.base/sun.misc=ALL-UNNAMED");
        }

        if (!"-".equals(properties.getCpuPreheat().getStartCron())) {
            chaosCronScheduler.schedule(properties.getCpuPreheat().getStartCron(), this::triggerCpuChaos);
            log.info("[Shanhai-SystemPreheat-CPU]-已注册独立 Cron:{}",properties.getCpuPreheat().getStartCron());
        }
        if (!"-".equals(properties.getMemoryPreheat().getStartCron())) {
            chaosCronScheduler.schedule(properties.getMemoryPreheat().getStartCron(), this::triggerMemoryChaos);
            log.info("[Shanhai-SystemPreheat-Memory]-已注册独立 Cron:{}",properties.getMemoryPreheat().getStartCron());
        }
    }

    private void triggerCpuChaos() {
        if (cpuRunning.compareAndSet(false, true)) {
            lifeCyclePool.submit(this::runCpuLifeCycle);
        }
    }

    private void triggerMemoryChaos() {
        if (memRunning.compareAndSet(false, true)) {
            lifeCyclePool.submit(this::runMemoryLifeCycle);
        }
    }

    private void runCpuLifeCycle() {
        log.info("[Shanhai-SystemPreheat-CPU]-启动 -> 进入拉升期");
        log.info("[Shanhai-SystemPreheat-CPU]-当前系统 CPU 使用率:{}%",SystemLoadUtils.getSystemCpuLoad());

        currentCpuPercent = 0;
        adjustCpuThreads();

        while (currentCpuPercent < properties.getCpuPreheat().getTargetPercent()) {
            currentCpuPercent = Math.min(properties.getCpuPreheat().getTargetPercent(),
                    currentCpuPercent + properties.getCpuPreheat().getStepPercent());
            log.info("[Shanhai-SystemPreheat-CPU]-步进拉升至:{}%",currentCpuPercent);
            try {
                Thread.sleep(properties.getCpuPreheat().getRampUpMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        log.info("[Shanhai-SystemPreheat-CPU]-达到峰值 {} % -> 保持 {}秒",currentCpuPercent,properties.getCpuPreheat().getHoldMs() / 1000);
        try {
            Thread.sleep(properties.getCpuPreheat().getHoldMs());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        log.info("[Shanhai-SystemPreheat-CPU]-保持期结束 -> 释放");
        currentCpuPercent = 0;
        stopCpuBurners();
        cpuRunning.set(false);
    }

    private void cpuBurnTask() {
        while (!Thread.currentThread().isInterrupted()) {
            double target = currentCpuPercent;
            if (target <= 0) {
                LockSupport.parkNanos(50_000_000L);
                continue;
            }
            double systemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
            int cores = Runtime.getRuntime().availableProcessors();
            if (systemLoad > cores * 0.85) {
                LockSupport.parkNanos(100_000_000L);
                continue;
            }

            long sliceNs = 10_000_000L;
            long busyNs = (long) (sliceNs * (target / 100.0));
            long sleepNs = sliceNs - busyNs;

            long start = System.nanoTime();
            double dummy = 1.0;
            while (System.nanoTime() - start < busyNs) {
                dummy *= Math.log(2.0);
            }
            if (dummy == Double.NaN){
                log.info("[Shanhai-SystemPreheat-CPU]-Never happen");
            }
            if (sleepNs > 0){
                LockSupport.parkNanos(sleepNs);
            }
        }
    }

    private void runMemoryLifeCycle() {
        log.info("[Shanhai-SystemPreheat-Memory]-启动 -> 进入拉升期");
        log.info("[Shanhai-SystemPreheat-Memory]-当前系统总内存: {} MB | 已使用:{} MB | 剩余可用: {} MB",SystemLoadUtils.getSystemTotalMemoryMB(),
                SystemLoadUtils.getSystemTotalMemoryMB()-SystemLoadUtils.getSystemFreeMemoryMB() ,SystemLoadUtils.getSystemFreeMemoryMB());
        currentMemoryMB = 0;
        long actualTargetMB = properties.getMemoryPreheat().getTargetMb();
        while (currentMemoryMB < actualTargetMB) {
            // 计算本次需要分配的内存 (最后一步可能不足 stepMb)
            long nextStep = Math.min(properties.getMemoryPreheat().getStepMb(), actualTargetMB - currentMemoryMB);

            // 动态安全校验
            if (!checkMemorySafety(nextStep)) {
                log.error("[Shanhai-SystemPreheat-Memory]-触发安全熔断，提前结束拉升-> 进入保持阶段");
                // 跳出拉升循环，进入保持阶段
                break;
            }
            // 执行分配
            allocateMemory(nextStep);
            try {
                Thread.sleep(properties.getMemoryPreheat().getRampUpMs());
            } catch (InterruptedException e) {
                return;
            }
        }

        log.info("[Shanhai-SystemPreheat-Memory]-达到峰值 {}MB -> 保持 {}秒",currentMemoryMB, properties.getMemoryPreheat().getHoldMs() / 1000);
        try {
            Thread.sleep(properties.getMemoryPreheat().getHoldMs());
        } catch (InterruptedException e) {
            return;
        }
        log.info("[Shanhai-SystemPreheat-Memory]-保持期结束 -> 释放");
        releaseMemory();
        memRunning.set(false);
    }
    /**
     * 分配前的动态内存安全检查
     * 1. 检查本次申请量是否超过当前剩余内存的安全比例。
     * 2. 检查申请后系统剩余内存是否会低于极低水位线。
     */
    private boolean checkMemorySafety(long nextStepMB) {
        long systemTotalMB = SystemLoadUtils.getSystemTotalMemoryMB();
        long systemFreeMB = SystemLoadUtils.getSystemFreeMemoryMB();
        double safeRatio = properties.getMemoryPreheat().getMaxSystemPercent() / 100.0;

        // 校验 1: 本次申请的内存不能超过当前系统剩余内存的 safeRatio (如 80%)
        if (nextStepMB > systemFreeMB * safeRatio) {
            log.error("[Shanhai-SystemPreheat-Memory]-本次申请 {}MB 超过系统剩余可用内存 {}MB 的安全比例，拒绝分配！", nextStepMB, systemFreeMB);
            return false;
        }

        // 校验 2: 申请后，系统剩余内存不得低于系统总内存的 (1 - safeRatio) (防止 OS 直接 OOM)
        long afterAllocFreeMB = systemFreeMB - nextStepMB;
        long minReservedMB = (long) (systemTotalMB * (1.0 - safeRatio));
        if (afterAllocFreeMB < minReservedMB) {
            log.error("[Shanhai-SystemPreheat-Memory]-分配后系统剩余 {} MB 将低于极低水位线 {}MB，拒绝分配！", afterAllocFreeMB, minReservedMB);
            return false;
        }
        return true;
    }

    private void allocateMemory(long mbToAllocate) {
        synchronized (memLock) {
            try {
                long bytes = mbToAllocate * 1024 * 1024;
                long ptr = unsafe.allocateMemory(bytes);
                for (long i = 0; i < bytes; i += 1024) {
                    unsafe.putByte(ptr + i, (byte) 1);
                }
                memoryPointers.add(ptr);
                currentMemoryMB += mbToAllocate;
                log.info("[Shanhai-SystemPreheat-Memory]- 步进拉升至: {} MB (系统剩余: {}  MB)", currentMemoryMB, SystemLoadUtils.getSystemFreeMemoryMB());
            } catch (OutOfMemoryError e) {
                log.error("[Shanhai-SystemPreheat-Memory]- JVM 层面抛出 OOM，底层分配失败！");
                e.printStackTrace();
                // 强制跳出外层 while 循环
                currentMemoryMB = properties.getMemoryPreheat().getTargetMb();
            }
        }
    }

    private void releaseMemory() {
        synchronized (memLock) {
            if (memoryPointers.isEmpty()) return;
            for (Long ptr : memoryPointers) {
                if (ptr != 0) unsafe.freeMemory(ptr);
            }
            memoryPointers.clear();
            currentMemoryMB = 0;
            log.info("[Shanhai-SystemPreheat-Memory]- 物理内存已彻底释放，RES 将瞬间下降");
        }
    }

    private void adjustCpuThreads() {
        if (cpuBurnerPool == null || cpuBurnerPool.isShutdown()) {
            cpuBurnerPool = Executors.newCachedThreadPool();
        }
        for (int i = 0; i < properties.getCpuPreheat().getThreads(); i++) {
            Thread t = new Thread(this::cpuBurnTask, "ShanHai-SystemPreheat-CPU-Burner");
            t.setPriority(Thread.MIN_PRIORITY);
            cpuBurnerPool.submit(t);
            activeCpuThreads.add(t);
        }
    }

    private void stopCpuBurners() {
        if (cpuBurnerPool != null) {
            cpuBurnerPool.shutdownNow();
            cpuBurnerPool = null;
        }
        activeCpuThreads.clear();
    }

    @PreDestroy
    public void destroy() {
        if (lifeCyclePool != null) {
            lifeCyclePool.shutdownNow();
        }
        stopCpuBurners();
        releaseMemory();
    }
}
