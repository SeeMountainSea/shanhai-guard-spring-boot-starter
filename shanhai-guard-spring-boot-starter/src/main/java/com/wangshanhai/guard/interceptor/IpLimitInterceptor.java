package com.wangshanhai.guard.interceptor;

import com.wangshanhai.guard.annotation.IpLimit;
import com.wangshanhai.guard.config.IpLimitConfig;
import com.wangshanhai.guard.utils.IpUtils;
import com.wangshanhai.guard.utils.ShanHaiGuardErrorCode;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


/**
 * IP限制拦截器
 * @author Fly.Sky
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(IpLimitConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
@ConditionalOnProperty(
        prefix = "shanhai.iplimit",
        name = "enable",
        havingValue = "true"
)
public class IpLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IpLimitConfig ipLimitConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        IpLimit ipLimit = handlerMethod.getMethodAnnotation(IpLimit.class);

        // 如果方法上没有注解，检查类上是否有注解
        if (ipLimit == null) {
            ipLimit = handlerMethod.getBeanType().getAnnotation(IpLimit.class);
        }

        // 如果没有IP限制注解或未启用，直接通过
        if (ipLimit == null || !ipLimit.enabled()) {
            return true;
        }

        // 获取配置的渠道
        String channel = ipLimit.channel();
        if (!ipLimitConfig.getChannels().containsKey(channel)) {
            log.warn("not find channel: {}", channel);
            throw new ShanHaiGuardException(ShanHaiGuardErrorCode.IP_FORBIDDEN,"请检查配置参数");
        }

        // 获取IP段配置
        String[] ipRanges = ipLimitConfig.getChannels().get(channel);
        if (ipRanges == null || ipRanges.length == 0) {
            log.warn("not find ip config for channel: {}", channel);
            throw new ShanHaiGuardException(ShanHaiGuardErrorCode.IP_FORBIDDEN,"请检查配置参数");
        }

        // 获取源IP
        String[] headers = ipLimit.headers().length > 0 ? ipLimit.headers() : new String[]{};
        String clientIp = IpUtils.getClientIp(request, headers);

        // 检查IP是否在白名单中
        boolean allowed = Arrays.stream(ipRanges)
                .anyMatch(range -> IpUtils.isIpInRange(clientIp, range));

        if (!allowed) {
            throw new ShanHaiGuardException(ShanHaiGuardErrorCode.IP_FORBIDDEN,"403 Forbidden");
        }
        return true;
    }

}