package com.wangshanhai.guard.service;

import java.util.Set;

/**
 * 加载敏感词
 * @author Shmily
 */
public interface SensitiveWordsDictService {
    /**
     * 获取敏感词数据
     * @return
     */
    public Set<String> loadDict();
}
