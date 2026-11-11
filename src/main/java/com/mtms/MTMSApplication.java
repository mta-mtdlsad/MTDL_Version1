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

    public static void main( String[] args ) {

        ConfigurableApplicationContext context = SpringApplication.run(MTMSApplication.class, args);
//        UDPReceiver receiver = context.getBean(UDPReceiver.class);
//        TrackSimulator simulator = context.getBean(TrackSimulator.class);
//
//        try {
//            // Start the receiver on a separate thread
//            Thread receiverThread = new Thread(() -> {
//                try {
//                    receiver.startReceiving();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // Allow the program to exit when the test finishes
//            //receiverThread.setDaemon(true);
//            receiverThread.start();
//            // Give the receiver time to start
//            Thread.sleep(1000);
//
//            int[] params = {
//                    3,          // Three sources
//                    5,          // Five Tracks per source
//                    100,        // 100 observations
//                    10,         // 10 mgs per sec
//            };
//            simulator.initializeSimulator("MIXED", 1, 12345L, params);
//
//            while (simulator.isRunning()) {
//                Thread.sleep(200);
//            }
//
//            simulator.initializeSimulator(null, 0, 12345L, null);
//
//            while (simulator.isRunning()) {
//                Thread.sleep(200);
//            }
//
//            receiver.stopReceiving();
//            receiverThread.join();
//
//            System.out.println("---- Test Completed ----");
//            System.out.println("Simulation finished");
//            System.out.println("UDP received");
//            System.out.println("Check PostgreSQL for saved Obs");
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
