package com.wangshanhai.guard.component;

import cn.hutool.json.JSONUtil;
import com.wangshanhai.guard.annotation.SensitiveBodyIngore;
import com.wangshanhai.guard.annotation.SensitiveTextBody;
import com.wangshanhai.guard.config.WordsSensitiveConfig;
import com.wangshanhai.guard.sensitive.Finder;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import com.wangshanhai.guard.utils.JsonUnescapeUtil;
import com.wangshanhai.guard.utils.Logger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *  自定义Body编码组件
 *
 * @author Fly.Sky
 */
@Configuration
@EnableConfigurationProperties(WordsSensitiveConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.sensitivewords",
        name = "enableResp",
        havingValue = "true")
@ControllerAdvice
public class RespBodySensitiveComponent implements ResponseBodyAdvice {

    @Autowired
    private  WordsSensitiveConfig wordsSensitiveConfig;
    @Override
    public boolean supports(MethodParameter methodParameter, Class converterType) {
        //判断是否对当前参数进行处理
        if(methodParameter.hasMethodAnnotation(SensitiveBodyIngore.class)){
            return false;
        }
        List<String> excludePathPatterns=wordsSensitiveConfig.getRespPathPatterns();
        if(excludePathPatterns!=null && !excludePathPatterns.isEmpty()){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Optional<String> t= excludePathPatterns.stream().filter(excludePathPattern -> request.getRequestURI().startsWith(excludePathPattern.replace("/**",""))).findFirst();
            return !t.isPresent();
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try{
            if(methodParameter.hasMethodAnnotation(SensitiveTextBody.class)){
                return findWordsSensitive(String.valueOf(body),wordsSensitiveConfig);
            }
            return JsonUnescapeUtil.parseNestedJson(findWordsSensitive(JSONUtil.toJsonStr(body),wordsSensitiveConfig));
        }catch (Exception e){
            Logger.error("[ RespBodySensitive-EncodeError]-msg:{}",e.getMessage());
        }
        return body;
    }
    public String findWordsSensitive(String source, WordsSensitiveConfig wordsSensitiveConfig){
        Set<String> sensitiveWords= Finder.find(source);
        if(sensitiveWords!=null&&sensitiveWords.size()>0){
            if(wordsSensitiveConfig.getSensitiveFilterMode()==1){
                Logger.error("[ShanhaiGuard-WordsSensitive-Scan-Alert],wordsSensitive:{}",sensitiveWords);
                return Finder.replace(source, '*');
            }else{
                throw  new ShanHaiGuardException(ShanHaiGuardErrorCode.WORDS_SENSITIVE_LIMIT,"请求包含敏感词："+ JSONUtil.toJsonStr(sensitiveWords));
            }
        }
        return source;
    }
}
