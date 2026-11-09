package com.mtms.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mtms")
public class MTMSApplication {
    public static void main( String[] args ) {
        SpringApplication.run(MTMSApplication.class, args);
    }
}
