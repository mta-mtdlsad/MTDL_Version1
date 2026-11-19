package com.mtms.network;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

@Component
public class UDPSender {
    private static final Logger log = LoggerFactory.getLogger(UDPSender.class);

    @Value("${mtms.udp.host:localhost}")
    private String host;

    @Value("${mtms.udp.port:9090}")
    private int port;

    public UDPSender() {}

    public void sendTrack(Track track) {
        if (track == null) return;

        String formattedTime = track.getCreatedTime() != null
                ? track.getCreatedTime().format(MTMSConstants.TIMESTAMP_FORMATTER).replace(" ", "T")
                : "";

        String message = String.format("%s|%d|%s|%.6f|%.6f|%.2f|%s|%s|%s|%s",
                track.getSourceId() != null ? track.getSourceId().trim() : "",
                track.getSequenceNumber() != null ? track.getSequenceNumber() : 0,
                track.getTrackNumber() != null ? track.getTrackNumber().trim() : "",
                track.getLatitude(),
                track.getLongitude(),
                track.getSpeed(),
                track.getIdentityType() != null ? track.getIdentityType().trim() : "",
                track.getTrackStatus() != null ? track.getTrackStatus().trim() : "",
                formattedTime,
                track.getPlatformType().name()
        );

        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            InetAddress address = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            log.error("Failed to transmit UDP datagram packet: {}", e.getMessage());
        }
    }
}
