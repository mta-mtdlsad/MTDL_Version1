package com.simulator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringJoiner;

public class TrackSimulator {
    private String[][] TDLs = {{"Link-A", "TRK1"}, {"Link-B", "TRK2"}, {"Link-C", "TRK3"}};
    private Integer[] sequenceIds = {1, 1, 1};
    private String[] identity_types = {"FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN"};
    private String[] track_statuses = {"ACTIVE", "INACTIVE"};
    private Random random = new Random(1234L);
    private ArrayList tracks = new ArrayList<>();

    public void getTracks(boolean realTime, String scenario) {
        if(realTime) simulateContinuousRun(scenario);
        else simulateLimitedRun(scenario);
    }

    private void simulateLimitedRun(String scenario) {
        System.out.println(simulateTrack(1));
        System.out.println(simulateTrack(1));
        System.out.println(simulateTrack(0));
    }

    private void simulateContinuousRun(String scenario) {
        int i = 0;
        while(true) {
            System.out.println(simulateTrack(random.nextInt(3)));
            i++;
            if(i == 100) break; // Change later to stop from frontend
        }
    }

    public String simulateTrack(int tdl) {
        StringJoiner joiner = new StringJoiner("|");
        Integer sequenceId;
        double latitude, longitude, speed;
        String track_number, identity_type, track_status;
        LocalDateTime observationTime;

        sequenceId = sequenceIds[tdl]++;
        track_number = TDLs[tdl][1] + String.format("%03d", random.nextInt(100));
        latitude = 10.0 + (random.nextInt(100) / 100.0);
        longitude = 70.0 + (random.nextInt(100) / 100.0);
        speed = 300.0 + (random.nextInt(1500));
        identity_type = identity_types[random.nextInt(4)];
        track_status = track_statuses[random.nextInt(2)];
        observationTime = LocalDateTime.now();

        joiner.add(TDLs[tdl][0]);
        joiner.add(String.valueOf(sequenceId));
        joiner.add(track_number);
        joiner.add(String.valueOf(latitude));
        joiner.add(String.valueOf(longitude));
        joiner.add(String.valueOf(speed));
        joiner.add(identity_type);
        joiner.add(track_status);
        joiner.add(String.valueOf(observationTime));

        return joiner.toString();
    }
}
