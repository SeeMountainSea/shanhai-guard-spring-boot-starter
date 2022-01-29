package com.sayrmb.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SayHiApp {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context= SpringApplication.run(SayHiApp.class, args);
        System.out.println(context.getEnvironment().getProperty("app.version"));
    }
}
