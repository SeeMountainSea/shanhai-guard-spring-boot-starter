package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.service.DecodeBodyService;
import org.springframework.stereotype.Service;

@Service
public class DecodeServiceImpl implements DecodeBodyService {
    @Override
    public String decodeRequestBody(String body) {
        return "[self-decode]"+body;
    }
}
