package com.wangshanhai.guard.component;

import cn.hutool.json.JSONUtil;
import com.wangshanhai.guard.annotation.DecodeBody;
import com.wangshanhai.guard.annotation.DecodeBodyIgnore;
import com.wangshanhai.guard.config.DecodeBodyConfig;
import com.wangshanhai.guard.sensitive.Finder;
import com.wangshanhai.guard.service.DecodeBodyService;
import com.wangshanhai.guard.utils.HttpBizException;
import com.wangshanhai.guard.utils.Logger;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 自定义Body解码规则
 * @author Shmily
 */
@Configuration
@EnableConfigurationProperties(DecodeBodyConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.decodebody",
        name = "enable",
        havingValue = "true")
@ControllerAdvice
public class DecodeBodyComponent extends RequestBodyAdviceAdapter {
    @Autowired
    private DecodeBodyService decodeBodyService;
    @Autowired
    private DecodeBodyConfig decodeBodyConfig;
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        //判断是否对当前参数进行处理
        if(methodParameter.hasMethodAnnotation(DecodeBodyIgnore.class)){
            return false;
        }
        if(decodeBodyConfig.getMode()==2&&!methodParameter.hasMethodAnnotation(DecodeBody.class)){
            return false;
        }
        List<String> excludePathPatterns=decodeBodyConfig.getExcludePathPatterns();
        if(excludePathPatterns!=null && !excludePathPatterns.isEmpty()){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Optional<String> t= excludePathPatterns.stream().filter(excludePathPattern -> request.getRequestURI().startsWith(excludePathPattern.replace("/**",""))).findFirst();
            return !t.isPresent();
        }
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try{
            String source= IOUtils.toString(inputMessage.getBody(), "utf-8");
            String sourceContent=null;
            if(decodeBodyConfig.getMode()==1){
                sourceContent=decodeBodyService.decodeRequestBody(source);
            }else {
                sourceContent=decodeBodyService.decodeRequestBody( parameter.getMethodAnnotation(DecodeBody.class).ruleId(),source);
            }
            if(decodeBodyConfig.getEnableSensitive()){
                Set<String> sensitiveWords=Finder.find(sourceContent);
                if(sensitiveWords!=null&&sensitiveWords.size()>0){
                    if(decodeBodyConfig.getSensitiveFilterMode()==1){
                        sourceContent=Finder.replace(sourceContent, '*');
                    }else{
                        throw  new HttpBizException("80002","请求包含敏感词："+ JSONUtil.toJsonStr(sensitiveWords));
                    }
                }
            }
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
            throw  new HttpBizException("80001","请求参数解析异常");
        }
    }
}
