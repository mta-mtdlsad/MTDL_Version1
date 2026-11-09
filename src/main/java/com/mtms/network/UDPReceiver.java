package com.mtms.network;

import com.mtms.model.Track;
import com.mtms.service.MessageParserService;
import com.mtms.service.MessageProcessorService;
import com.mtms.service.SourceHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UDPReceiver {
    private static final int PORT = 5000;
    // Queue holds a maximum of 1000 messages.
    public final BlockingQueue<ReceivedMessage> queue = new ArrayBlockingQueue<>(1000);


    // Store each message with its source and receive time.
    private record ReceivedMessage(String text, String sourceId, LocalDateTime receivedAt) {}
    @Autowired
    private MessageParserService parser;
    @Autowired
    private MessageProcessorService processor;
    @Autowired
    private SourceHealthService sourceHealth;


    // Runtime counters.
    public final AtomicLong receivedCount = new AtomicLong();
    public final AtomicLong acceptedCount = new AtomicLong();
    public final AtomicLong rejectedCount = new AtomicLong();
    public final AtomicLong duplicateCount = new AtomicLong();
    public final AtomicLong outOfOrderCount = new AtomicLong();
    public final AtomicLong droppedCount = new AtomicLong();
    public final AtomicLong failureCount = new AtomicLong();
    private volatile boolean running = false;
    private volatile boolean stopRequested = false;
    private volatile DatagramSocket socket;
    private Thread workerThread;

    // Extract source ID before full message validation.
    private String extractSourceId(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        String[] fields = message.split("\\|", -1);
        if (fields.length > 0) {
            String sourceId = fields[0].trim();
            if (sourceId.matches("[A-Za-z0-9_-]{2,30}")) {
                return sourceId;
            }
        }
        return null;
    }

    public void startReceiving() throws Exception {
        if (stopRequested) {
            return;
        }
        // Open UDP port.
        DatagramSocket newSocket = new DatagramSocket(PORT);
        socket = newSocket;
        try {
            if (stopRequested) {
                return;
            }
            running = true;
            // Register simulator sources.
            // Keep these IDs consistent with the simulator.
//            sourceHealth.registerSource("LINK-A");
//            sourceHealth.registerSource("LINK-B");
//            sourceHealth.registerSource("LINK-C");

            // Start source health monitoring.
            sourceHealth.startMonitoring();

            // Start worker for validation and database operations.
            workerThread = new Thread(this::processQueue, "UDP-Processing-Worker");

            workerThread.start();
            System.out.println("UDP Receiver listening on port " + PORT);
            while (running && !stopRequested) {
                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive UDP message.
                newSocket.receive(packet);
                receivedCount.incrementAndGet();
                LocalDateTime receivedAt = LocalDateTime.now();
                String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);

                // Extract the source ID.
                String sourceId = extractSourceId(message);

                // Update source activity immediately.
                if (sourceId != null) {
                    sourceHealth.recordReceived(sourceId, receivedAt);
                }

                // Store message and metadata together.
                ReceivedMessage item = new ReceivedMessage(message, sourceId, receivedAt);

                // Add message to queue without waiting.
                boolean queued = queue.offer(item);
                String reply;
                if (queued) {
                    reply = "QUEUED";
                } else {
                    droppedCount.incrementAndGet();
                    reply = "QUEUE_FULL";
                }

                // Send acknowledgement to UDP sender.
                byte[] replyBytes = reply.getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(replyBytes, replyBytes.length, packet.getAddress(), packet.getPort());
                newSocket.send(response);
            }
        } catch (SocketException e) {
            // Closing socket during shutdown is normal.
            if (!stopRequested) {
                throw e;
            }
        } finally {
            running = false;
            newSocket.close();

            // Finish processing queued messages.
            if (workerThread != null) {
                workerThread.join();
            }

            // Stop source monitoring.
            sourceHealth.stopMonitoring();
            printStatistics();
            sourceHealth.printSourceStatus();
        }
    }

    // Worker handles validation and database operations.
    private void processQueue() {
        while (running || !queue.isEmpty()) {
            ReceivedMessage item = null;
            try {
                item = queue.poll(
                        200,
                        TimeUnit.MILLISECONDS
                );
                if (item == null) {
                    continue;
                }
                String outcome;
                int sequence = 0;
                try {
                    // Parse and validate incoming message.
                    Track track = parser.parse(item.text());
                    sequence = track.getSequenceNumber();

                    // Save Track and classify the outcome.
                    MessageProcessorService.ProcessingResult result = processor.processDetailed(track);
                    outcome = result.outcome().name();
                    switch (result.outcome()) {
                        case ACCEPTED -> {
                            acceptedCount.incrementAndGet();
                            System.out.println("Track saved.");
                            if (result.outOfOrder()) {
                                outOfOrderCount.incrementAndGet();
                            }
                        }
                        case DUPLICATE -> {
                            duplicateCount.incrementAndGet();
                            failureCount.incrementAndGet();
                            System.out.println("Track not saved (duplicate).");
                        }
                        case FAILURE -> {
                            System.out.println("Track not saved (processing failure).");
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid or malformed UDP message.
                    rejectedCount.incrementAndGet();
                    failureCount.incrementAndGet();
                    outcome = "REJECTED";
                    System.out.println("Rejected UDP message: " + e.getMessage());
                }

                // Save processing outcome to sources table.
                if (item.sourceId() != null) {
                    sourceHealth.recordOutcome(item.sourceId(), item.receivedAt(), outcome, sequence);
                }
            } catch (InterruptedException e) {
                // Continue until pending messages are processed.
                if (!running && queue.isEmpty()) {
                    break;
                }
            } catch (Exception e) {
                // Continue processing other messages.
                System.err.println(
                        "Worker error: " + e.getMessage()
                );
            }
        }
        System.out.println("UDP worker stopped.");
    }

    // Stop receiving new UDP messages.
    public void stopReceiving() {
        stopRequested = true;
        running = false;
        DatagramSocket current = socket;
        if (current != null) {
            current.close();
        }
    }

    // Print UDP statistics.
    public void printStatistics() {
        System.out.println("\n========== UDP STATISTICS ==========");
        System.out.println("Received: " + receivedCount.get());
        System.out.println("Accepted: " + acceptedCount.get());
        System.out.println("Rejected: " + rejectedCount.get());
        System.out.println("Duplicates: " + duplicateCount.get());
        System.out.println("Out of order: " + outOfOrderCount.get());
        System.out.println("Failures: " + failureCount.get());
        System.out.println("Dropped: " + droppedCount.get());
        System.out.println("Pending: " + queue.size());
    }



    public SourceHealthService getSourceHealth() {
        return sourceHealth;
    }
}
