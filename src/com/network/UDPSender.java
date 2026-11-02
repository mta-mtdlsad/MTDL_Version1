package com.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender {
    private static final int PORT = 5000;
    public boolean sendMessage(String message) throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        try (DatagramSocket socket = new DatagramSocket()) {
            // Stop waiting after 3 seconds
            socket.setSoTimeout(3000);
            InetAddress address = InetAddress.getByName("127.0.0.1");
            // Send the UDP message
            DatagramPacket packet = new DatagramPacket(
                    data, data.length, address, PORT
            );
            socket.send(packet);
            // Receive acknowledgement from UDP receiver
            byte[] buffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(
                    buffer, buffer.length
            );
            socket.receive(response);
            String result = new String(
                    response.getData(),
                    response.getOffset(),
                    response.getLength(),
                    StandardCharsets.UTF_8
            );
            // Return true only when processing succeeds
            return result.equals("SUCCESS");
        }
    }
}