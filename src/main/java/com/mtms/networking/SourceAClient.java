package com.mtms.networking;

import java.io.IOException;
import java.net.*;
import java.util.List;
import com.mtms.simulator.TrackSimulator;
import org.springframework.stereotype.Component;

@Component
public class SourceAClient {
    static List<String> listOfTracks = null;

    public void sendingDataToServer() {

        // generate tracks from source
        listOfTracks = TrackSimulator.generateTracksA();

        // socket object to carry data
        DatagramSocket datagramSocket;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        // get local host ip
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // create buffer
        byte[] buff = null;

        // create datagram packet to send the data
        DatagramPacket datagramPacket = null;
        listOfTracks.add("end");

        // sending simulated tracks
        for (String track : listOfTracks) {
            buff = track.getBytes();
            datagramPacket = new DatagramPacket(buff, buff.length, ip, 1234);
            try {
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
