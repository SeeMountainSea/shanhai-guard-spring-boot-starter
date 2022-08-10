package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.service.RespGuardRuleDefService;
import org.springframework.stereotype.Service;

@Service
public class OpenRespGuardRuleDefService implements RespGuardRuleDefService {
    @Override
    public Object jsonGenerator(String ruleId, Object fieldValue) {
        return String.valueOf(fieldValue)+"@"+ruleId;
    }
}
