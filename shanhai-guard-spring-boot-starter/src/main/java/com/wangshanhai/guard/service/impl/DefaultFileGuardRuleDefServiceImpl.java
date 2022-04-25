package com.wangshanhai.guard.service.impl;

import com.wangshanhai.guard.service.FileGuardRuleDefService;
import com.wangshanhai.guard.utils.HttpBizException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 默认实现
 */
public class DefaultFileGuardRuleDefServiceImpl implements FileGuardRuleDefService {
    @Override
    public boolean isSafe(Map<String, MultipartFile> files) {
        throw new HttpBizException("请先实现自定义校验规则校验器后再启用该组件!");
    }
}
