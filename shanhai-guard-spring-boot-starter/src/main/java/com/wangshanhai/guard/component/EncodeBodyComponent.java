package com.wangshanhai.guard.component;

import cn.hutool.json.JSONUtil;
import com.wangshanhai.guard.annotation.EncodeBody;
import com.wangshanhai.guard.annotation.EncodeBodyIgnore;
import com.wangshanhai.guard.config.EncodeBodyConfig;
import com.wangshanhai.guard.service.EncodeBodyService;
import com.wangshanhai.guard.utils.HttpBizException;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 *  自定义Body编码组件
 *
 * @author Fly.Sky
 */
@Configuration
@EnableConfigurationProperties(EncodeBodyConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.encodebody",
        name = "enable",
        havingValue = "true")
@ControllerAdvice
public class EncodeBodyComponent implements ResponseBodyAdvice {
    @Autowired
    private EncodeBodyConfig encodeBodyConfig;
    @Autowired
    private EncodeBodyService encodeBodyService;
    @Override
    public boolean supports(MethodParameter methodParameter, Class converterType) {
        //判断是否对当前参数进行处理
        if(methodParameter.hasMethodAnnotation(EncodeBodyIgnore.class)){
            return false;
        }
        if(encodeBodyConfig.getMode()==2&&!methodParameter.hasMethodAnnotation(EncodeBody.class)){
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try{
            String respBody=null;
            if(encodeBodyConfig.getMode()==1){
                respBody=encodeBodyService.encodeRespBody(JSONUtil.toJsonStr(body));
            }else {
                respBody=encodeBodyService.encodeRespBody( methodParameter.getMethodAnnotation(EncodeBody.class).ruleId(),JSONUtil.toJsonStr(body));
            }
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return respBody;
        }catch (Exception e){
            Logger.error("[Resp-Body-EncodeError]-msg:{}",e.getMessage());
            throw  new HttpBizException("80002","响应参数解析异常");
        }
    }
}
