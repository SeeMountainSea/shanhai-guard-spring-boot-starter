package com.wangshanhai.examples.config;

import com.wangshanhai.guard.annotation.EnableShanHaiGuard;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableShanHaiGuard
@MapperScan({"com.wangshanhai.examples.mapper"})
public class GuardConfig  implements WebMvcConfigurer {
}
