package com.wangshanhai.guard.service;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * XSS与SQL注入检测
 * @author Shmily
 */
public class XssCleanService  extends HttpServletRequestWrapper {

    private static String key = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";
    private static Set<String> notAllowedKeyWords = new HashSet<String>(0);
    private static String replacedString = "INVALID";
    private final byte[] body; //用于保存读取body中数据
    private String currentUrl;

    static {
        String keyStr[] = key.split("\\|");
        for (String str : keyStr) {
            notAllowedKeyWords.add(str);
        }
    }

    public XssCleanService(HttpServletRequest request) throws IOException {
        super(request);
        currentUrl = request.getRequestURI();
        body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    /**
     * @Description 覆盖getParameter方法，将参数和参数值做xss过滤
     * @Date 2020/5/20 9:57
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        return cleanXss(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String,String[]> values = super.getParameterMap();
        if(null == values){
            return null;
        }
        Map<String,String[]> result = new HashMap<>();
        for (String key : values.keySet()) {
            String encodedKey = cleanXss(key);
            int count = values.get(key).length;
            String[] encodedValues = new String[count];
            for(int i = 0;i < count;i++){
                encodedValues[i] = cleanXss(values.get(key)[i]);
            }
            result.put(encodedKey,encodedValues);
        }
        return result;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        return cleanXss(value);
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
            encodedValues[i] = cleanXss(values[i]);
        }
        return encodedValues;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
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

    /**
     * Xss 检测
     */
    private String cleanXss(String valueP){
        String value = valueP.replaceAll("<","&lt;").replaceAll(">","&gt;");
        value = value.replaceAll("<","& lt;").replaceAll(">","& gt;");
        value = value.replaceAll("\\(","& #40;").replaceAll("\\)","& #41;");
        value = value.replaceAll("'","& #39;");
        value = value.replaceAll("eval\\((.*)\\)","");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']","\"\"");
        value = value.replaceAll("script","");
        if(!value.equals(valueP)){
            Logger.error("[Xss-Scan-Alert]-url:{},value{}",this.currentUrl,valueP);
        }
        value = cleanSqlKeyWords(value);
        return value;
    }

    /**
     * SQL注入检测
     * @param value
     * @return
     */
    private String cleanSqlKeyWords(String value){
        String paramValue = value;
        for (String keyWord : notAllowedKeyWords) {
            if(paramValue.length() > keyWord.length() + 4
                    && (paramValue.contains(" " + keyWord)
                    || paramValue.contains(keyWord + " ")
                    || paramValue.contains(" " + keyWord + " "))){
                paramValue = StringUtils.replace(paramValue,keyWord,replacedString);
                Logger.error("[SQL-Scan-Alert]-url:{},sqlKey:{},value:{}",this.currentUrl,keyWord,value);
            }
        }
        return paramValue;
    }
}
