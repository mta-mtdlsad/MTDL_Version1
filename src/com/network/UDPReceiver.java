package com.network;
import com.domain.Track;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class UDPReceiver {

    private static final int PORT = 5000;

    // The queue can hold a maximum of 1000 messages. This prevents unlimited memory usage.
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);

    private final MessageParser parser = new MessageParser();
    // Checks duplicates and message ordering
    private final MessageProcessor processor = new MessageProcessor();

    // Counters for monitoring. Health
    //AtomicLong: allows multiple threads to safely read and modify a long value without the risk of data corruption or race conditions.
    private final AtomicLong receivedCount = new AtomicLong();
    private final AtomicLong droppedCount = new AtomicLong();
    private final AtomicLong rejectedCount = new AtomicLong();
    private final AtomicLong processedCount = new AtomicLong();

    private volatile boolean running = false;

    private DatagramSocket socket;
    private Thread workerThread;

    public void startReceiving() throws Exception {

        // Open UDP port 5000.
        socket = new DatagramSocket(PORT);
        running = true;

        // Start the worker that processes queued messages.
        workerThread = new Thread(() -> {
            // Continue until stopped AND queue is empty.
            while (running || !queue.isEmpty()) {
                try {
                    // Take one message from the queue.
                    String message = queue.poll(200, TimeUnit.MILLISECONDS
                    );
                    if (message == null) {
                        continue;
                    }

                    // Validate the UDP message.
                    Track track = parser.parse(message);

                    // Process and save using existing code. DB writes happen in this worker, not in the UDP receiving loop.
                    boolean saved = processor.process(track);

                    if (saved) {
                        processedCount.incrementAndGet();
                        System.out.println("Track saved.");
                    } else {
                        rejectedCount.incrementAndGet();
                        System.out.println("Track not saved (duplicate or error).");
                    }

                } catch (IllegalArgumentException e) {
                    // Invalid messages must not stop the worker.
                    rejectedCount.incrementAndGet();
                    System.out.println("Invalid message: " + e.getMessage());

                } catch (InterruptedException e) {
                    // Recheck whether we should continue.
                    // Do not discard queued messages.
                } catch (Exception e) {
                    // Continue processing other messages.
                    System.err.println("Processing error: " + e.getMessage());
                }
            }
            System.out.println("Processing worker stopped.");
        }, "UDP-Worker");

        workerThread.start();

        System.out.println("UDP Receiver listening on port " + PORT);

        try {
            while (running) {

                // Buffer for one UDP packet.
                byte[] buffer = new byte[4096];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive a message. No dp operation happens here.
                socket.receive(packet);

                receivedCount.incrementAndGet();

                // Convert UDP bytes into UTF-8 text.
                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                // Put the message into the queue immediately.
                // offer() does not wait for PostgreSQL.
                boolean added = queue.offer(message);

                String response;

                if (added) {
                    // NOT yet saved to PostgreSQL.
                    response = "queue accepted into memory";
                } else {
                    droppedCount.incrementAndGet();
                    response = "The receiver accepted the message into its queue";
                }

                // Send an immediate queue-status response.
                byte[] responseData = response.getBytes(StandardCharsets.UTF_8);

                DatagramPacket responsePacket =
                        new DatagramPacket(
                                responseData,
                                responseData.length,
                                packet.getAddress(),
                                packet.getPort()
                        );
                socket.send(responsePacket);
            }

        } catch (SocketException e) {
            // Closing the socket during shutdown is normal.
            if (running) { throw e;}

        } finally {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // Finish processing messages already in the queue.
            if (workerThread != null) {workerThread.join();}
            printStatistics();
        }
    }

    // Called when Tomcat shuts down.
    public void stopReceiving() {
        running = false;
        // Closing the socket unblocks socket.receive().
        if (socket != null) {
            socket.close();
        }
    }

    public void printStatistics() {
        System.out.println("\n---- UDP Statistics ----");
        System.out.println("Received: " + receivedCount.get());
        System.out.println("Saved: " + processedCount.get());
        System.out.println("Rejected: " + rejectedCount.get());
        System.out.println("Dropped (queue full): " + droppedCount.get());
        System.out.println("Pending in queue: " + queue.size());
        System.out.println("Duplicates: " + processor.getDuplicateCount());
        System.out.println("Out of order: " + processor.getOutOfOrderCount());
    }
}
