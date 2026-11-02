package com.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender {
    private static final int PORT = 5000;
    public void sendMessage(String message) throws Exception {
        // Convert the message into bytes
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        // Create a UDP socket
        try (DatagramSocket socket = new DatagramSocket()) {
            // Address of the receiving computer
            InetAddress address = InetAddress.getByName("127.0.0.1");
            // Create a packet containing the message
            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    address,
                    PORT
            );
            // Send the packet to the receiver
            socket.send(packet);
            System.out.println("Sent: " + message);
        }
    }
}