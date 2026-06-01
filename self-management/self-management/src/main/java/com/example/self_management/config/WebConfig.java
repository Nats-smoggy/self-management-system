package com.example.self_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 告诉 Spring Boot 这是一个配置类，启动时要加载它
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有接口跨域
                .allowedOriginPatterns("*") // 允许任意前端地址（比如我们的 localhost:5173）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方式
                .allowCredentials(true) // 允许携带 Cookie 等凭证
                .maxAge(3600); // 预检请求的有效期
    }
}