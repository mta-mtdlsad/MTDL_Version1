package com.mtms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication()
public class MTMSApplication {
    public static void main( String[] args ) throws Exception {
        // Start Spring Boot
        ConfigurableApplicationContext context = SpringApplication.run(MTMSApplication.class, args);
    }
}
