package com.mtms.network;

import com.mtms.service.MessageProcessorService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class UDPReceiver {
    private static final Logger log = LoggerFactory.getLogger(UDPReceiver.class);

    @Value("${mtms.udp.port:9090}")
    private int port;

    private final MessageProcessorService messageProcessorService;
    private DatagramSocket socket;
    private ThreadPoolExecutor workerExecutor;
    private volatile boolean running = false;

    public UDPReceiver(MessageProcessorService messageProcessorService) {
        this.messageProcessorService = messageProcessorService;
    }

    @PostConstruct
    public synchronized void startReceiver() {
        if (running) return;

        try {
            this.socket = new DatagramSocket(port);
            this.running = true;

            int cores = Runtime.getRuntime().availableProcessors();
            this.workerExecutor = new ThreadPoolExecutor(
                    cores,
                    Math.max(cores * 4, 32),
                    60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(1000),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

            Thread listenerThread = new Thread(this::listenLoop, "UDP-Listener-Thread");
            listenerThread.setDaemon(true);
            listenerThread.start();
            log.info("UDP Ingestion Server started on port: {}", port);
        } catch (SocketException e) {
            log.error("Failed to bind UDP socket on port {}: {}", port, e.getMessage());
        }
    }

    private void listenLoop() {
        byte[] buffer = new byte[2048];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String payload = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                workerExecutor.submit(() -> {
                    try {
                        System.out.println("Received Packet: " + payload);
                        messageProcessorService.processRawPayload(payload);
                    } catch (Exception e) {
                        log.error("Worker thread error processing payload: {}", e.getMessage(), e);
                    }
                });

            } catch (IOException e) {
                if (!running) {
                    log.info("UDP Socket closed cleanly.");
                } else {
                    log.error("Error receiving UDP packet: {}", e.getMessage());
                }
            }
        }
    }

    @PreDestroy
    public synchronized void stopReceiver() {
        if (!running) return;
        running = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        if (workerExecutor != null) {
            workerExecutor.shutdown();
            try {
                if (!workerExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                    workerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                workerExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("UDP Receiver shut down cleanly.");
    }
}
