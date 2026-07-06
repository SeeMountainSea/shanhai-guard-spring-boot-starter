package com.wangshanhai.guard.utils;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * 内存检测工具
 * @author Fly.Sky
 */
public class SystemLoadUtils {
    /**
     * 兼容 JDK 8/17 获取系统总CPU
     */
    public static double getSystemCpuLoad() {
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof OperatingSystemMXBean) {
            OperatingSystemMXBean sunOsBean = (OperatingSystemMXBean) osBean;

            // 获取系统 CPU 使用率 (返回值在 0.0 到 1.0 之间)
            double cpuLoad = sunOsBean.getSystemCpuLoad();
            return  cpuLoad * 100;
        }
        return Double.MAX_VALUE;
    }
    /**
     * 兼容 JDK 8/17 获取系统总物理内存
     */
    public static long getSystemTotalMemoryMB() {
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof OperatingSystemMXBean) {
            long totalBytes = ((OperatingSystemMXBean) osBean).getTotalPhysicalMemorySize();
            return totalBytes / (1024 * 1024);
        }
        return Long.MAX_VALUE;
    }

    /**
     * 兼容 JDK 8/17 获取系统当前可用物理内存
     */
    public static long getSystemFreeMemoryMB() {
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof OperatingSystemMXBean) {
            long freeBytes = ((OperatingSystemMXBean) osBean).getFreePhysicalMemorySize();
            return freeBytes / (1024 * 1024);
        }
        return Long.MAX_VALUE;
    }
}
