package com.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender {

    private static final int PORT = 5000;

    public boolean sendMessage(String message) throws Exception {

        // Convert the message into bytes.
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        try (DatagramSocket socket = new DatagramSocket()) {

            // Maximum time to wait for queue acknowledgement (3 sec).
            socket.setSoTimeout(3000);

            InetAddress address = InetAddress.getByName("127.0.0.1");

            // Create the UDP packet (message)
            DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);

            // Send the message to UDPReceiver.
            socket.send(packet);

            // Wait for a quick queue-status response - acknowledgement from UDP receiver
            byte[] buffer = new byte[1024];

            DatagramPacket response = new DatagramPacket(buffer, buffer.length);

            socket.receive(response);

            String result = new String(
                    response.getData(),
                    response.getOffset(),
                    response.getLength(),
                    StandardCharsets.UTF_8
            );

            // QUEUED: The receiver accepted the message into its queue.
            // It does NOT mean PostgreSQL saved the Track.
            return result.equals("The receiver accepted the message into its queue.");
        }
    }
}
