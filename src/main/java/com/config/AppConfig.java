package com.config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.repository",
        "com.service",
        "com.network"
})
public class AppConfig {
}