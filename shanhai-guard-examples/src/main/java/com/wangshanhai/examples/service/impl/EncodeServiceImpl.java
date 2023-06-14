package com.wangshanhai.examples.service.impl;

import com.wangshanhai.guard.service.EncodeBodyService;
import org.springframework.stereotype.Service;

/**
 * The {@code EncodeServiceImpl} class
 *
 * @author Fly.Sky
 * @since 2023/6/14 10:53
 */
@Service
public class EncodeServiceImpl implements EncodeBodyService {
    @Override
    public String encodeRespBody(String body) {
        return "encode@"+body;
    }

    @Override
    public String encodeRespBody(String ruleId, String body) {
        return "encode@"+ruleId+"@"+body;
    }
}
