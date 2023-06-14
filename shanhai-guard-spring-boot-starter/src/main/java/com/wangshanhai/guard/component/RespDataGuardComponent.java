package com.wangshanhai.guard.component;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.wangshanhai.guard.config.RespGuardConfig;
import com.wangshanhai.guard.resp.RespDataAnnotationIntrospector;
import com.wangshanhai.guard.service.RespGuardRuleDefService;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * 响应报文脱敏组件
 * @author Shmily
 */
@Configuration
@ConditionalOnProperty(
        prefix = "shanhai.respguard",
        name = "enable",
        havingValue = "true")
@EnableConfigurationProperties(RespGuardConfig.class)
public class RespDataGuardComponent {
    @Autowired
    private RespGuardRuleDefService respGuardRuleDefService;
    @Autowired
    private RespGuardConfig respGuardConfig;
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        Logger.info("[RespData-Guard-Init]-init Component");
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        AnnotationIntrospector source = objectMapper.getSerializationConfig().getAnnotationIntrospector();
        AnnotationIntrospector target = AnnotationIntrospectorPair.pair(source, new RespDataAnnotationIntrospector(respGuardConfig,respGuardRuleDefService));
        objectMapper.setAnnotationIntrospector(target);
        return objectMapper;
    }
}
