package com.wangshanhai.examples.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wangshanhai.guard.mybatis.audit.result.OperationResult;
import com.wangshanhai.guard.service.ShanHaiDataAuditService;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.stereotype.Service;

@Service
public class DBAuditServiceImpl  implements ShanHaiDataAuditService {
    @Override
    public void dealOperationResult(OperationResult operationResult) {
        Logger.info("[db-audit]-data:{}", JSONObject.toJSONString(operationResult));
    }
}
