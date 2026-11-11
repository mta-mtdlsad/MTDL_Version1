package com.mtms.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Server {


    private final TrackService trackService;

    public Server(TrackService trackService) {
        this.trackService = trackService;
    }

//    @Autowired
//    private TrackService trackService;

    public void recevingDataFromClient()  {

        // creating socket to listen for data
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(1234);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        // creating buffer
        byte[] receive = new byte[65535];

        // creating packet to receive data
        DatagramPacket datagramPacket = null;

        while (true) {
            datagramPacket = new DatagramPacket(receive, receive.length);

            //retrieving data
            try {
                datagramSocket.receive(datagramPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (byteToString(receive).toString().equals("end")){
                break;
            }

            trackService.processTrackString(byteToString(receive));

            // clear buffer
            receive = new byte[65535];
        }
    }

    // convert
    private static String byteToString(byte[] data) {
        if (data == null)
            return null;
        StringBuilder string = new StringBuilder();
        int i = 0;
        while(data[i] != 0){
            string.append((char) data[i]);
            i++;
        }
        return string.toString();
    }
}
