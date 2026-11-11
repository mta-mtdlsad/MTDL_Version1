package com.mtms;

import com.mtms.network.UDPReceiver;
import com.mtms.service.SourceHealthService;
import com.mtms.simulator.TrackSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication(scanBasePackages = "com.mtms")
public class MTMSApplication {

    public static void main( String[] args ) throws Exception {

        // Start Spring Boot
        ConfigurableApplicationContext context = SpringApplication.run(MTMSApplication.class, args);
        // Get the simulator from Spring
        TrackSimulator simulator = context.getBean(TrackSimulator.class);
        // Start the simulator
        simulator.initializeSimulator("MIXED", 1, 12345L, new int[]{3, 2, 20, 100});

    }
}
