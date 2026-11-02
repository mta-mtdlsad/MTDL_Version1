package com.network;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener

public class UDPStartupListener implements ServletContextListener {
    private Thread receiverThread;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Start the UDP receiver when Tomcat deploys the application
        receiverThread = new Thread(() -> {
            try {
                new UDPReceiver().startReceiving();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiverThread.setName("UDP-Receiver");
        receiverThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // We will add proper receiver shutdown handling next
    }
}
