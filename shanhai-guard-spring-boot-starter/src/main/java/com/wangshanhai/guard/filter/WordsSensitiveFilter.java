package com.wangshanhai.guard.filter;

import com.wangshanhai.guard.config.WordsSensitiveConfig;
import com.wangshanhai.guard.filter.wapper.WordsSensitiveWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *  全量参数敏感词扫描器
 *
 * @author Fly.Sky
 */
public class WordsSensitiveFilter implements Filter {
    private final WordsSensitiveConfig wordsSensitiveConfig;
    public WordsSensitiveFilter(WordsSensitiveConfig wordsSensitiveConfig) {
        this.wordsSensitiveConfig=wordsSensitiveConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        WordsSensitiveWrapper wordsSensitiveWrapper = new WordsSensitiveWrapper(httpRequest,wordsSensitiveConfig);
        chain.doFilter(wordsSensitiveWrapper,response);
    }
}
