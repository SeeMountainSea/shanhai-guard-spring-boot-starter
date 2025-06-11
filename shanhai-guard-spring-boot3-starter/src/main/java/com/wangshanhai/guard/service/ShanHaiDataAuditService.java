package com.wangshanhai.guard.service;

import com.wangshanhai.guard.mybatis.audit.result.OperationResult;

/**
 * @author Fly.Sky
 */
public interface ShanHaiDataAuditService {

    /**
     * 处理操作结果
     * @param operationResult
     */
    public void dealOperationResult(OperationResult operationResult);
}
