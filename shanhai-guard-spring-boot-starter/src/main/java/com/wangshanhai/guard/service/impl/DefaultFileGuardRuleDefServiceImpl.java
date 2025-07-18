package com.wangshanhai.guard.service.impl;

import com.wangshanhai.guard.service.FileGuardRuleDefService;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 默认实现
 * @author Shmily
 */
public class DefaultFileGuardRuleDefServiceImpl implements FileGuardRuleDefService {
    @Override
    public boolean isSafe(String ruleId, Map<String, MultipartFile> files) {
        throw new ShanHaiGuardException(ShanHaiGuardErrorCode.COMPONENT_INIT_ERROR,"请先实现自定义校验规则校验器后再启用该组件!");
    }
}
