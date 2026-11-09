package com.mtms.network;

import com.mtms.model.ValidationResult;
import com.mtms.service.AlertService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UDPStartupListener implements ServletContextListener {
    private Thread receiverThread;
    // object for alert service to create required alerts
    @Autowired
    private AlertService alertService;
    @Autowired
    private UDPReceiver receiver;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Start the existing UDP reciever thread
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

//        // Stop accepting new UDP messages.
//        if (receiver != null) {
//            receiver.stopReceiving();
//        }

//        // Wait for the receiver to finish. It also waits for its processing worker.
//        if (receiverThread != null) {
//            try {
//                receiverThread.join();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
        if(springContext !=null){
            springContext.close();
        }
        System.out.println("UDP Receiver stopped.");
    }
}
