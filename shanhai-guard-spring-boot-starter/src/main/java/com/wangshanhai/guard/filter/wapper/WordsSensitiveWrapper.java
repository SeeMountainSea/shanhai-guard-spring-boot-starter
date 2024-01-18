package com.wangshanhai.guard.filter.wapper;

import cn.hutool.json.JSONUtil;
import com.wangshanhai.guard.config.WordsSensitiveConfig;
import com.wangshanhai.guard.sensitive.Finder;
import com.wangshanhai.guard.utils.HttpBizException;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 参数敏感词检测
 *
 * @author Fly.Sky
 */
public class WordsSensitiveWrapper extends HttpServletRequestWrapper {
    private WordsSensitiveConfig wordsSensitiveConfig;
    private String currentUrl;
    /**
     * 用于保存读取body中数据
     */
    private final byte[] body;

    public WordsSensitiveWrapper(HttpServletRequest request,WordsSensitiveConfig wordsSensitiveConfig) throws IOException {
        super(request);
        this.wordsSensitiveConfig=wordsSensitiveConfig;
        this.currentUrl = request.getRequestURI();
        body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    /**
     * 覆盖getParameter方法
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        return findWordsSensitive(value,wordsSensitiveConfig);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String,String[]> values = super.getParameterMap();
        if(null == values){
            return null;
        }
        Map<String,String[]> result = new HashMap<>();
        for (String key : values.keySet()) {
            String encodedKey = findWordsSensitive(key,wordsSensitiveConfig);
            int count = values.get(key).length;
            String[] encodedValues = new String[count];
            for(int i = 0;i < count;i++){
                encodedValues[i] = findWordsSensitive(values.get(key)[i],wordsSensitiveConfig);
            }
            result.put(encodedKey,encodedValues);
        }
        return result;
    }


    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if(StringUtils.isEmpty(values)){
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0;i < count;i++) {
            encodedValues[i] = findWordsSensitive(values[i],wordsSensitiveConfig);
        }
        return encodedValues;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body.length>0?findWordsSensitive(new String(body),wordsSensitiveConfig).getBytes():body);
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener arg0) {

            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String findWordsSensitive(String source,WordsSensitiveConfig wordsSensitiveConfig){
        Set<String> sensitiveWords= Finder.find(source);
        if(sensitiveWords!=null&&sensitiveWords.size()>0){
            if(wordsSensitiveConfig.getSensitiveFilterMode()==1){
                Logger.error("[ShanhaiGuard-WordsSensitive-Scan-Alert]-url:{},wordsSensitive:{}",this.currentUrl,sensitiveWords);
                return Finder.replace(source, '*');
            }else{
                throw  new HttpBizException("80002","请求包含敏感词："+ JSONUtil.toJsonStr(sensitiveWords));
            }
        }
        return source;
    }
}
