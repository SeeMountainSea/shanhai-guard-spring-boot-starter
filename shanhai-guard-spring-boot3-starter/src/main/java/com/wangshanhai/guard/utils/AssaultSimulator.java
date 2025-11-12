package com.wangshanhai.guard.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 混沌模拟器
 * @author Fly.Sky
 */
public class AssaultSimulator {
    private final SecureRandom secureRandom;
    private final int batchSize;
    private final int minExceptions;
    private final int maxExceptions;

    private int requestCounter;
    private int currentExceptionCount;
    private List<Boolean> currentBatch;

    /**
     * 构造函数 - 使用默认配置
     */
    public AssaultSimulator() {
        this(100, 3, 5);
    }

    /**
     * 构造函数 - 完全可配置
     * @param batchSize 批次大小
     * @param minExceptions 最小次数
     * @param maxExceptions 最大次数
     */
    public AssaultSimulator(int batchSize, int minExceptions, int maxExceptions) {
        if (batchSize <= 0) {
            throw new ShanHaiGuardException(ShanHaiGuardErrorCode.ASSAULT_SIMULATOR_ERROR,"批次大小必须大于0");
        }
        if (minExceptions < 0 || maxExceptions < minExceptions || maxExceptions > batchSize) {
            throw new ShanHaiGuardException(ShanHaiGuardErrorCode.ASSAULT_SIMULATOR_ERROR,"模拟次数范围无效");
        }

        this.secureRandom = new SecureRandom();
        this.batchSize = batchSize;
        this.minExceptions = minExceptions;
        this.maxExceptions = maxExceptions;
        this.requestCounter = 0;
        generateNewBatch();
    }

    /**
     * 生成新的批次配置
     */
    private void generateNewBatch() {
        // 生成异常的具体次数
        this.currentExceptionCount = minExceptions + secureRandom.nextInt(maxExceptions - minExceptions + 1);

        // 创建结果列表
        List<Boolean> batch = new ArrayList<>();

        // 添加异常结果
        for (int i = 0; i < currentExceptionCount; i++) {
            batch.add(true);
        }

        // 添加正常结果
        for (int i = currentExceptionCount; i < batchSize; i++) {
            batch.add(false);
        }

        // 使用加密安全的洗牌算法打乱顺序
        Collections.shuffle(batch, secureRandom);

        this.currentBatch = batch;
        this.requestCounter = 0;
        Logger.debug("[AssaultSimulator]-总次数:{},模拟次数:{} ",batchSize,currentExceptionCount);
    }

    /**
     * 模拟单次请求 - 核心方法
     */
    public boolean makeRequest() {
        if (requestCounter >= batchSize) {
            generateNewBatch();
        }

        boolean result = currentBatch.get(requestCounter);
        requestCounter++;

        return result;
    }

    /**
     * 批量模拟请求
     * @param count 请求次数
     * @return 请求结果列表
     */
    public List<Boolean> makeRequests(int count) {
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(makeRequest());
        }
        return results;
    }

    /**
     * 获取当前批次状态
     */
    public String getBatchStatus() {
        int currentExceptions = 0;
        for (int i = 0; i < requestCounter; i++) {
            if (currentBatch.get(i)) {
                currentExceptions++;
            }
        }

        return String.format("批次进度: %d/%d, 已发生异常: %d/%d",
                requestCounter, batchSize, currentExceptions, currentExceptionCount);
    }

    /**
     * 获取配置信息
     */
    public String getConfiguration() {
        return String.format("%d|%d-%d",
                batchSize, minExceptions, maxExceptions);
    }

    /**
     * 重置模拟器状态
     */
    public void reset() {
        generateNewBatch();
    }
}
