package com.network;

import com.domain.Track;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UDPReceiver {
    private static final int PORT = 5000;
    public void startReceiving() throws Exception {
        // Open the port to receive UDP messages
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            MessageParser parser = new MessageParser();
            // Checks duplicates and message ordering
            MessageProcessor processor = new MessageProcessor();
            String result;

            System.out.println("UDP Receiver listening on port " + PORT);
            // Keep receiving messages
            while (true) {
                // Create a buffer to hold incoming data
                byte[] buffer = new byte[4096];
                // Create a packet to receive data
                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length
                );
                // Wait for an incoming UDP packet
                socket.receive(packet);
                // Convert the received bytes into text
                String message = new String(
                        packet.getData(),
                        packet.getOffset(),
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );
                try {
                    // Parse and validate the message
                    Track track = parser.parse(message);
                    // Process and save the track
                    boolean saved = processor.process(track);
                    result = saved ? "SUCCESS" : "ERROR";
                } catch (Exception e) {
                    System.out.println("Rejected UDP message: " + e.getMessage());
                    result = "ERROR";
                }
                // Send the result back to the sender
                byte[] responseData = result.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(
                        responseData,
                        responseData.length,
                        packet.getAddress(),
                        packet.getPort()
                );
                socket.send(responsePacket);
            }
        }
    }
}