package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class TrackAnalyticsService {

    private final TrackRepository trackRepository;

    public TrackAnalyticsService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    // Display report statistics
    public List<String> showReportStat() {

        List<String> report = new ArrayList<>();

        report.add("Hostile Tracks:");
        report.addAll(showHostileTracks());
        report.add("------------------------<pre>");

        report.add("High Speed Tracks:");
        report.addAll(showHighSpeedTracks());
        report.add("------------------------<pre>");

        report.add("Active Tracks:");
        report.addAll(showActiveTracks());
        report.add("------------------------<pre>");

        report.add("Inactive Tracks:");
        report.addAll(showInactiveTracks());
        report.add("------------------------<pre>");

        report.add("Identity Count:");
        report.addAll(showIdentityCount());

        return report;
    }
    // function to show hostile tracks
    public List<String> showHostileTracks() {

        List<Track> tracks = trackRepository.findByIdentity("HOSTILE");
        List<String> result = new ArrayList<>();

        if (tracks != null && !tracks.isEmpty()) {
            for (Track track : tracks) {
                result.add(track.toString());
            }
        } else {
            result.add("No hostile tracks available");
        }

        return result;
    }

    // function to show high-speed tracks
    public List<String> showHighSpeedTracks() {

        List<Track> tracks = trackRepository.findBySpeedGreaterThanEqual(700);
        List<String> result = new ArrayList<>();

        if (tracks != null && !tracks.isEmpty()) {
            for (Track track : tracks) {
                result.add(track.toString());
            }
        } else {
            result.add("No available tracks with high speed");
        }

        return result;
    }
    // function to show identity counts
    public List<String> showIdentityCount() {

        List<Map<String, Integer>> counts = trackRepository.findIdentityCount();
        List<String> result = new ArrayList<>();
        counts.forEach(row -> {result.add(row.get("identity") + ": " + row.get("count"));});
        return result;
    }

//    // function to show identity counts
//    public List<String> showIdentityCount() {
//
//        Map<String, Integer> counts = trackRepository.findIdentityCounts();
//        List<String> result = new ArrayList<>();
//
//        if (counts != null && !counts.isEmpty()) {
//            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
//                result.add(entry.getKey() + ": " + entry.getValue());
//            }
//        } else {
//            result.add("No identity counts available");
//        }
//
//        return result;
//    }

//    // function to show tracks by identity
//    public List<Track> showTracksByIdentity(String identity) {
//        List<Track> listOfTracks = trackRepository.getTracksByIdentity(identity);
//
//        if (listOfTracks != null) {
//            for (Track track : listOfTracks) {
//                System.out.println(track);
//            }
//        } else {
//            System.out.println("No tracks available");
//        }
//        return listOfTracks;
//    }

    // Retrieve active tracks
    public List<String> showActiveTracks() {

        List<Track> tracks = trackRepository.findByTrackStatus("ACTIVE");
        List<String> result = new ArrayList<>();

        if (tracks != null && !tracks.isEmpty()) {
            for (Track track : tracks) {
                result.add(track.toString());
            }
        } else {
            result.add("No available active tracks");
        }

        return result;
    }
    // Retrieve inactive tracks
    public List<String> showInactiveTracks() {

        List<Track> tracks = trackRepository.findByTrackStatus("INACTIVE");
        List<String> result = new ArrayList<>();

        if (tracks != null && !tracks.isEmpty()) {
            for (Track track : tracks) {
                result.add(track.toString());
            }
        } else {
            result.add("No available inactive tracks");
        }

        return result;
    }

//    // Retrieve tracks with minimum speed
//    public static List<Track> showTracksByMinimumSpeed(int minimumSpeed) {
//        return trackRepository.getTracksByMinimumSpeed(minimumSpeed);
//    }
//
//    //retrieve the total number of tracks for each identity
//    public Map<String, Integer> getIdentitySummary() {
//        return trackRepository.getTrackIdentity();
//    }


    // Generating cross-source correlation tracks
    public List<String> getCorrelatedTracks(){
        List<String> correlatedTracksInfo = new ArrayList<String>();
        List<Track> listOfTracks = new ArrayList<>();
        trackRepository.findAll().forEach(listOfTracks::add);
        // each track compared to other tracks
        for (Track track : listOfTracks) {
            for (Track track1 : listOfTracks) {
                if (track != track1) {
                    Duration timeDifference = Duration.between(track.getObservationTime(),
                            track1.getObservationTime());
                    double speedDifference = Math.abs(track.getSpeed() - track1.getSpeed());
                    double distanceDifference = calculateDistance(track.getLatitude(), track.getLongitude(),
                            track1.getLatitude(), track1.getLongitude());

                    // filtering tracks to find matches
                    // not yet done: fixing time and double results
                    if(
                            (track.getTrackNumber().equals(track1.getTrackNumber())) &&
                                    (!track.getSourceId().equals(track1.getSourceId())) &&
                                    (track.getIdentity().equals(track1.getIdentity())) &&
                                    (speedDifference < 100) &&
                                    (distanceDifference < 2) &&
                                    ((timeDifference.compareTo(Duration.ofSeconds(30)) <= 0) && timeDifference.getSeconds()>=0)
                    ){
                        correlatedTracksInfo.add(
                                "\nTracks SourceId: " + track.getSourceId() + ", " + track1.getSourceId() +
                                        "\nTrack Number: " + track.getTrackNumber() +
                                        "\nFirst Track Position: " + track.getLatitude() + ", " + track.getLongitude() +
                                        "\nSecond Track Position: " + track1.getLatitude() + ", " + track1.getLongitude() +
                                        "\nTime Difference: " + timeDifference.getSeconds()+ " seconds" +
                                        "\nSpeed Difference: " + speedDifference +
                                        "\nDistance: " + distanceDifference + " km"
                        );
                    }
                }
            }
        }
        System.out.println("Displaying matching tracks");
        for(String matches : correlatedTracksInfo)
            System.out.println(matches);

        return correlatedTracksInfo;
    }

    // calculation to find the distance between two points
    public double calculateDistance(double latitude1, double longtitude1, double latitude2, double longtitude2){
        double radLatitude1 = Math.toRadians(latitude1);
        double radLongtitude1 = Math.toRadians(longtitude1);
        double radLatitude2 = Math.toRadians(latitude2);
        double radLongtitude2 = Math.toRadians(longtitude2);
        double xAxis = (radLongtitude2 - radLongtitude1) * Math.cos((radLatitude1 + radLatitude2)/2);
        double yAxis = (radLatitude2 - radLatitude1);
        return Math.sqrt(xAxis*xAxis + yAxis*yAxis) * 6371; //earth radius
    }

}