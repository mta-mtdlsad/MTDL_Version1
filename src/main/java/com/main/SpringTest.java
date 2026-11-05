package com.main;

import com.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringTest {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            System.out.println("Spring Framework started successfully!");
        }
    }
}