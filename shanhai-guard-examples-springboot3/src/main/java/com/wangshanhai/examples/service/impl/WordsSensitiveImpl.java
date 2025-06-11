package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.service.SensitiveWordsDictService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class WordsSensitiveImpl implements SensitiveWordsDictService {
    @Override
    public Set<String> loadDict() {
        Set<String> dict=new HashSet<>();
        dict.add("今天");
        dict.add("烦死了");
        dict.add("更美好");
        return dict;
    }
}
