package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.WebGuardConfig;
import com.wangshanhai.guard.filter.WebScanFilter;
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
 * SQL & XSS 注入检测
 * @author Shmily
 */
@Configuration
@EnableConfigurationProperties(WebGuardConfig.class)
@ConditionalOnProperty(
        prefix = "shanhai.webguard",
        name = "enable",
        havingValue = "true")
public class WebGuardComponent {
    @Autowired
    private WebGuardConfig webGuardConfig;
    /**
     * 注册安全过滤器
     * @return
     */
    @Bean("WebScanFilterRegistrationBean")
    public FilterRegistrationBean<WebScanFilter> filterRegistration(){
        Logger.info("[ShanhaiGuard-WebGuard-Init]-init Component");
        //创建并注册WebScanFilter
        FilterRegistrationBean<WebScanFilter> reFilter = new FilterRegistrationBean<WebScanFilter>();
        reFilter.setFilter(new WebScanFilter());
        for(String urlPattern:webGuardConfig.getPathPatterns()){
            //拦截的路径（对所有请求拦截）
            reFilter.addUrlPatterns(urlPattern);
        }
        //拦截器的名称
        reFilter.setName("WebScanFilter");
        return  reFilter;

    }

    @Bean
    @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
    public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter();
    }
}
