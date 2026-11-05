package com.network;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;



public class UDPSender {

    private static final int PORT = 5000;

    public boolean sendMessage(String message) throws Exception {
        // Convert message into bytes.
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        try (DatagramSocket socket = new DatagramSocket()) {
            // Maximum wait for acknowledgement: 3 seconds.
            socket.setSoTimeout(3000);
            InetAddress address = InetAddress.getByName("127.0.0.1");
            // Create UDP packet.
            DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);
            // Send message to UDPReceiver.
            socket.send(packet);
            // Wait for queue acknowledgement.
            byte[] buffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            String result = new String(response.getData(), response.getOffset(), response.getLength(), StandardCharsets.UTF_8);
            // QUEUED means accepted into memory,
            // not necessarily saved in PostgreSQL.
            return result.equals("QUEUED");
        }
    }
}