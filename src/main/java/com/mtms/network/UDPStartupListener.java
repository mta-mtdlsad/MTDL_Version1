package com.mtms.network;

import com.mtms.model.GeoPath;
import com.mtms.simulator.RandomPathGenerator;
import com.mtms.util.ValidationResult;
import com.mtms.service.AlertService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UDPStartupListener implements ServletContextListener {
    private Thread receiverThread;
    // object for alert service to create required alerts
    @Autowired
    private AlertService alertService;
    @Autowired
    private UDPReceiver receiver;

    public List<GeoPath> allPaths;;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Start the existing UDP reciever thread
        receiverThread = new Thread(() -> {
            try {
                receiver.startReceiving();
            } catch (Exception e) {
                ValidationResult result = new ValidationResult("UDP Receiver error:","HIGH");
                System.out.println(result.toString());
                alertService.createAlert(result);
                System.err.println("UDP Receiver error: " + e.getMessage());
            }
        }, "UDP-Receiver");

        receiverThread.start();
        generatePaths();
        System.out.println("UDP Receiver started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Stopping UDP Receiver ...");
        System.out.println("UDP Receiver stopped.");
    }

    protected void generatePaths() {
        double[] lat = {23.5, 25.5, 24.0};
        double[] lon = {54.5, 54.0, 54.0};
        String[] type = {"LAND", "SEA", "AIR"};
        allPaths = new ArrayList<>();
        int numOfPaths = 0;
        int radiusKm = 0;

        for(int i = 0; i < lat.length; i++) {
            if(i == 2) {
                numOfPaths = 40;
                radiusKm = 250;
            }
            else {
                numOfPaths = 30;
                radiusKm = 150;
            }
            RandomPathGenerator pathGenerator = new RandomPathGenerator();
            List<GeoPath> paths = pathGenerator.generateRandomPaths(
                    lat[i],
                    lon[i],
                    radiusKm,
                    numOfPaths,
                    12,
                    12,
                    type[i]
            );
            //sourcesAndPaths.put(sources[i], paths);
            allPaths.addAll(paths);
        }
        Collections.shuffle(allPaths);
    }

    public List<GeoPath> getPaths() {return allPaths;}
}
