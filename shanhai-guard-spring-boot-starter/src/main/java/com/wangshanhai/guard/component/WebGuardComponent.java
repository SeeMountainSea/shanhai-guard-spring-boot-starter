package com.wangshanhai.guard.component;

import com.wangshanhai.guard.config.WebGuardConfig;
import com.wangshanhai.guard.filter.WebScanFilter;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SQL & XSS 注入检测
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
     * 注册过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegiste(){
        Logger.info("[Web-Guard-Init]-init Component");
        FilterRegistrationBean reFilter = new FilterRegistrationBean();
        reFilter.setFilter(new WebScanFilter()); //创建并注册WebScanFilter
        for(String urlPattern:webGuardConfig.getPathPatterns()){
            reFilter.addUrlPatterns(urlPattern); //拦截的路径（对所有请求拦截）
        }
        reFilter.setName("WebScanFilter"); //拦截器的名称
        return  reFilter;

    }

}
