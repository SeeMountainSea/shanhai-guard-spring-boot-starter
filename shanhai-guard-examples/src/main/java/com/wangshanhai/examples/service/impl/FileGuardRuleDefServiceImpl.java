package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.service.FileGuardRuleDefService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileGuardRuleDefServiceImpl implements FileGuardRuleDefService {
    /**
     * 表单key及对应的文件
     * @param files 文件清单
     * @return
     */
    @Override
    public boolean isSafe(Map<String, MultipartFile> files) {
        System.out.println(files);
        return true;
    }
}
