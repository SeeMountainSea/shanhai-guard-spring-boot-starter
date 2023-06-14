package com.wangshanhai.guard.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 使用自定义规则校验文件上传
 * 使用场景:文件分片上传或其他需要的场景
 * @author Shmily
 */
public interface FileGuardRuleDefService {
    /**
     * 校验文件是否符合要求
     * @param ruleId 自定义规则ID
     * @param files 文件清单
     * @return
     */
    public boolean isSafe(String ruleId,Map<String, MultipartFile> files);
}
