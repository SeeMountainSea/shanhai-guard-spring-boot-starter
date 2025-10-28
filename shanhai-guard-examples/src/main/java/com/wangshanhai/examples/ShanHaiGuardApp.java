package com.wangshanhai.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Guard组件测试
 * @author guard
 */
@SpringBootApplication
public class ShanHaiGuardApp {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context= SpringApplication.run(ShanHaiGuardApp.class, args);
        System.out.println(context.getEnvironment().getProperty("app.version"));
    }
}
