
package com.network;

import com.domain.ValidationResult;
import com.service.AlertService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class UDPStartupListener implements ServletContextListener {
    private Thread receiverThread;
    // Keep a reference so we can stop the receiver.
    private UDPReceiver receiver;
    // object for alert service to create required alerts
    private final AlertService alertService = new AlertService();
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Create the UDP receiver.
        receiver = new UDPReceiver();
        // Start it in a background thread (when Tomcat deploys the application).
        receiverThread = new Thread(() -> {
            try {
                receiver.startReceiving();
            } catch (Exception e) {
                ValidationResult result = new ValidationResult("UDP Receiver error:","HIGH");
                System.out.println(result.toString());
                alertService.createAlert(result);
                System.err.println("UDP Receiver error: " + e.getMessage());
            }
        }, "UDP-Receiver");

        receiverThread.start();
        System.out.println("UDP Receiver started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Stopping UDP Receiver ...");

        // Stop accepting new UDP messages.
        if (receiver != null) {
            receiver.stopReceiving();
        }

        // Wait for the receiver to finish. It also waits for its processing worker.
        if (receiverThread != null) {
            try {
                receiverThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("UDP Receiver stopped.");
    }
}
