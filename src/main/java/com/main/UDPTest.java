package com.main;

import com.config.AppConfig;
import com.network.UDPReceiver;
import com.network.UDPSender;
import com.service.SourceHealthService;
import com.simulator.TrackSimulator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UDPTest {
    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            UDPReceiver receiver = context.getBean(UDPReceiver.class);
            TrackSimulator simulator_random = new TrackSimulator(context.getBean(SourceHealthService.class));
            TrackSimulator simulator_predefined = new TrackSimulator(context.getBean(SourceHealthService.class));

            // Start the receiver on a separate thread
            Thread receiverThread = new Thread(() -> {
                try {
                    receiver.startReceiving();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

// Allow the program to exit when the test finishes
//receiverThread.setDaemon(true);
            receiverThread.start();
// Give the receiver time to start
            Thread.sleep(1000);


            int[] params = {
                    3,          // Three sources
                    5,          // Five Tracks per source
                    100,        // 100 observations
                    10,         // 10 mgs per sec
            };
            simulator_random.initializeSimulator("MIXED", 1, 12345L, params);

            while (simulator_random.isRunning()) {
                Thread.sleep(200);
            }

            simulator_predefined.initializeSimulator(null, 0, 12345L, null);

            while (simulator_predefined.isRunning()) {
                Thread.sleep(200);
            }

            receiver.stopReceiving();
            receiverThread.join();

            System.out.println("---- Test Completed ----");
            System.out.println("Simulation finished");
            System.out.println("UDP received");
            System.out.println("Check PostgreSQL for saved Obs");

        }
    }
}