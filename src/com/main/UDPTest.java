package com.main;

import com.network.UDPReceiver;
import com.network.UDPSender;
import com.simulator.TrackSimulator;

public class UDPTest {
    public static void main(String[] args) throws Exception {
        UDPReceiver receiver = new UDPReceiver();

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


        TrackSimulator simulator = new TrackSimulator();
        UDPSender sender = new UDPSender();

        /*// Correlation
        sender.sendMessage(
                "LINK-A|1|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:15:30"
        );
        sender.sendMessage(
                "LINK-B|5|TRK2001|12.971700|77.594700|455|FRIEND|ACTIVE|2026-11-02T09:15:35"
        );*/

        simulator.startLimited(
                3,          // Three sources
                5,          // Five Tracks per source
                100,        // 100 observations
                10,         // 10 mgs per sec
                "NORMAL",   // Scenario
                12345L      // Seed
        );

        while (simulator.isRunning()){
            Thread.sleep(200);
        }


        // First observation
        sender.sendMessage(
                "LINK-A|1|TRK1001|12.971600|77.594600|450|FRIEND|ACTIVE|2026-11-02T09:15:30"
        );
        // Higher sequence number
        sender.sendMessage(
                "LINK-A|5|TRK1001|12.971700|77.594700|455|FRIEND|ACTIVE|2026-11-02T09:15:35"
        );
        // Duplicate: sequence 5 already exists
        sender.sendMessage(
                "LINK-A|5|TRK1001|12.971700|77.594700|455|FRIEND|ACTIVE|2026-11-02T09:15:35"
        );
        // Out of order: sequence 3 arrives after sequence 5
        sender.sendMessage(
                "LINK-A|3|TRK1001|12.971800|77.594800|460|FRIEND|ACTIVE|2026-11-02T09:15:40"
        );
        // Different source: sequence 5 is allowed
        sender.sendMessage(
                "LINK-B|5|TRK1001|12.972000|77.595000|450|FRIEND|ACTIVE|2026-11-02T09:15:45"
        );
        // Different source: speed invalid
        sender.sendMessage(
                "LINK-B|60|TRK1001|12.972000|77.595000|3000|FRIEND|ACTIVE|2026-11-02T09:15:45"
        );
        // Different source: sequence 5 is allowed
        sender.sendMessage(
                "LINK-B|6|TRK1001|12.972000|77.595000|500|HOSTILE|ACTIVE|2026-11-02T09:15:45"
        );
        sender.sendMessage(
                "LINK-B|6|TRK1001|12.972000|77.595000|500|HOSTILE|ACTIVE|2026-11-02T09:15:45|9"
        );

        receiver.stopReceiving();
        receiverThread.join();

        System.out.println("---- Test Completed ----");
        System.out.println("Simulation finished");
        System.out.println("UDP received");
        System.out.println("Check PostgreSQL for saved Obs");

    }
}