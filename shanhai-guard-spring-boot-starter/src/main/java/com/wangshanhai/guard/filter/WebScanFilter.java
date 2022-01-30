package com.wangshanhai.guard.filter;

import com.wangshanhai.guard.service.XssCleanService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Web参数扫描过滤器
 * 支持过滤XSS & SQL 注入检测
 */
public class WebScanFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //sql、xss过滤
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XssCleanService xssHttpServletRequestWrapper = new XssCleanService(httpRequest);
        chain.doFilter(xssHttpServletRequestWrapper,response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
