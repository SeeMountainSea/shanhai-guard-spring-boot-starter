package com.wangshanhai.guard.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * 内存压力测试器
 * @author Fly.Sky
 */
@Slf4j
public class MemoryStressor {
    private final List<byte[]> memoryBlocks = new ArrayList<>();
    // 10MB/块
    private static final int BLOCK_SIZE = 10 * 1024 * 1024;
    private volatile boolean allocated = false;

    /**
     * 申请内存到目标使用率
     * @param targetPercent 目标内存使用率(0-100)
     */
    public void allocate(int targetPercent) {
        if (allocated) {
            return;
        }

        Runtime rt = Runtime.getRuntime();
        long maxMem = rt.maxMemory();
        long currentUsed=rt.totalMemory()  - rt.freeMemory();
        // 85%安全缓冲
        long targetBytes = (long)(maxMem * (targetPercent / 100.0 * 0.85));
        log.info("[内存] 已使用:{} MB, 目标使用率: {}% -> {} MB", currentUsed/(1024 * 1024), targetPercent, targetBytes / (1024 * 1024));
        while ((currentUsed = rt.totalMemory()  - rt.freeMemory())  < targetBytes) {
            try {
                memoryBlocks.add(new  byte[BLOCK_SIZE]);
                // 每分配100MB打印进度
                if (memoryBlocks.size()  % 10 == 0) {
                    log.info("[内存] 已使用:{} MB, 已分配: {} MB",currentUsed/(1024 * 1024), ((long)memoryBlocks.size()  * BLOCK_SIZE) / (1024 * 1024));
                }
            } catch (OutOfMemoryError e) {
                log.error("[内存] 内存分配失败，已达到上限");
                break;
            }
        }
        allocated = true;
        printMemoryStats("分配完成");
    }

    /** 释放内存并触发GC */
    public void release() {
        memoryBlocks.clear();
        // 触发垃圾回收
        System.gc();
        allocated = false;
        try {
            // 等待GC完成
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        printMemoryStats("释放完成");
    }

    private void printMemoryStats(String phase) {
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memBean.getHeapMemoryUsage();
        long usedMB = heapUsage.getUsed()  / (1024 * 1024);
        long maxMB = heapUsage.getMax()  / (1024 * 1024);
        log.info("[内存] {}  ➤ 使用: {} MB / 最大: {} MB ({}%)",
                phase, usedMB, maxMB, (usedMB * 100.0 / maxMB));
    }
}
