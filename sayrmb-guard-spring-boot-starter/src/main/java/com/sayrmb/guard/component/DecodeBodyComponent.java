package com.sayrmb.guard.component;

import com.sayrmb.guard.config.DecodeBodyConfig;
import com.sayrmb.guard.service.DecodeBodyService;
import org.apache.commons.io.IOUtils;
import com.sayrmb.guard.annotation.DecodeBodyIgnore;
import com.sayrmb.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 自定义Body解码规则
 */
@Configuration
@EnableConfigurationProperties(DecodeBodyConfig.class)
@ConditionalOnProperty(
        prefix = "sayhi.decodebody",
        name = "enable",
        havingValue = "true")
@ControllerAdvice
public class DecodeBodyComponent extends RequestBodyAdviceAdapter {
    @Autowired
    private DecodeBodyService decodeBodyService;
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        //判断是否对当前参数进行处理
        if(methodParameter.hasMethodAnnotation(DecodeBodyIgnore.class)){
            return false;
        }
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try{
            String source= IOUtils.toString(inputMessage.getBody(), "utf-8");
            String sourceContent=decodeBodyService.decodeRequestBody(source);
            InputStream decodeInputStream=IOUtils.toInputStream(sourceContent, StandardCharsets.UTF_8.name());
            HttpInputMessage httpInputMessage=new HttpInputMessage() {
                    @Override
                    public InputStream getBody() throws IOException {
                        return decodeInputStream;
                    }
                    @Override
                    public HttpHeaders getHeaders() {
                        return inputMessage.getHeaders();
                    }
            };
            return super.beforeBodyRead(httpInputMessage, parameter, targetType, converterType);
        }catch (Exception e){
            Logger.error("[Request-Body-DecodeError]-msg:{}",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
