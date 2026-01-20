package com.wangshanhai.examples.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wangshanhai.guard.service.EncodeBodyService;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;


/**
 * EncodeServiceImpl
 * @author demo
 */
@Service
public class EncodeServiceImpl implements EncodeBodyService {
    @Override
    public Object encodeRespBody(Object body, ServerHttpResponse response) {
        JSONObject resp= JSONUtil.parseObj(JSONUtil.toJsonStr(body));
        resp.set("tips","encodeRespBody");
        return JSONUtil.toJsonStr(resp);
    }

    @Override
    public Object encodeRespBody(String ruleId, Object body,ServerHttpResponse response) {
        JSONObject resp= JSONUtil.parseObj(JSONUtil.toJsonStr(body));
        resp.set("tips","encodeRespBody@ruleId="+ruleId);
        return JSONUtil.toJsonStr(resp);
    }
}
