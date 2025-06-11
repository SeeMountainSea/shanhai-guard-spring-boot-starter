package com.wangshanhai.guard.filter;

import com.wangshanhai.guard.filter.wapper.WebScanWrapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Web参数扫描过滤器
 * 支持过滤XSS & SQL 注入检测
 * @author Shmily
 */
public class WebScanFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //sql、xss过滤
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        WebScanWrapper xssHttpServletRequestWrapper = new WebScanWrapper(httpRequest);
        chain.doFilter(xssHttpServletRequestWrapper,response);
    }
}
