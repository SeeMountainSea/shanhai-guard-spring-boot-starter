package com.wangshanhai.examples.config;

import com.alibaba.fastjson2.JSONObject;
import com.wangshanhai.guard.utils.ShanHaiGuardException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * GlobalErrorHandlerConfig
 * @author demo
 */
@RestControllerAdvice
@Order(-Integer.MAX_VALUE)
public class GlobalErrorHandlerConfig {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JSONObject errorHandler(Exception e) {
        JSONObject resp=new JSONObject();
        e.printStackTrace();
        if (e instanceof ShanHaiGuardException) {
            resp.put("code", ((ShanHaiGuardException) e).getCode());
            resp.put("message", e.getMessage());
        } else{
            resp.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            resp.put("message","INTERNAL_SERVER_ERROR");
        }
        return  resp;
    }
}
