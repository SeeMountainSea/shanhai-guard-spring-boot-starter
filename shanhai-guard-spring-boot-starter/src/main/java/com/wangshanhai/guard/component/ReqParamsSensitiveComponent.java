package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.WordsSensitiveConfig;
import com.wangshanhai.guard.filter.WordsSensitiveFilter;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * 敏感词检测组件
 * @author Shmily
 */
@Configuration
@EnableConfigurationProperties(WordsSensitiveConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.sensitivewords.enableReq",
        name = "enable",
        havingValue = "true")
public class ReqParamsSensitiveComponent {

    @Autowired
    private WordsSensitiveConfig wordsSensitiveConfig;
    /**
     * 注册安全过滤器
     * @return
     */
    @Bean("WordsSensitiveFilterRegistrationBean")
    public FilterRegistrationBean<WordsSensitiveFilter> filterRegistration(){
        Logger.info("[ShanhaiGuard-WordsSensitive]-init Component");
        //创建并注册WordsSensitiveFilter
        FilterRegistrationBean<WordsSensitiveFilter> reFilter = new FilterRegistrationBean<WordsSensitiveFilter>();
        reFilter.setFilter(new WordsSensitiveFilter(wordsSensitiveConfig));
        for(String urlPattern:wordsSensitiveConfig.getReqPathPatterns()){
            //拦截的路径（对所有请求拦截）
            reFilter.addUrlPatterns(urlPattern);
        }
        //拦截器的名称
        reFilter.setName("WordsSensitiveFilter");
        return  reFilter;

    }

    @Bean
    @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }
}
