package com.mtms.service;

import com.mtms.domain.Track;
import com.mtms.domain.Alert;
import com.mtms.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrackService {
    static TrackRepository trackRepository = new TrackRepository();
    static TrackAnalyticsService trackAnalyticsService = new TrackAnalyticsService();
    private static List<Track> listOfTracks = null;
    private static List<Track> outOfOrderTracks = new ArrayList<Track>();
    // calculating duplicates and out-of-order messages
    private static int duplicatesCount = 0;
    private static int orderCount = 0;

    // Validate and process each track
    public static void processTrackString(String trackString) {

        // parsing track from string
        List<String> malformedErrors = ValidationService.parseTrackString(trackString);

        // getting source id and track number from malformed track
        if (!malformedErrors.isEmpty()) {
            String sourceId = null;
            String trackNumber = null;
            try{
                sourceId = trackString.substring(trackString.indexOf("LINK"), trackString.indexOf("LINK")+6);
            } catch (Exception e) {
                malformedErrors.add("Source Id not found");
            }
            try{
                trackNumber = trackString.substring(trackString.indexOf("TRK"), trackString.indexOf("TRK")+7);
            } catch (Exception e) {
                malformedErrors.add("Track Number not found");
            }
            // creating alert
            AlertService.processAlert(sourceId, trackNumber, String.join(", ", malformedErrors), "Malformed message");
        } else {

            // parsing and sending track object to processing
            Track track = parsingTrackString(trackString);
            processTrack(track);
        }
    }

    //overloading
    // Validate and process each track object
    public static boolean processTrack(Track track) {

        List<String> validationErrors = ValidationService.validateTrack(track);

        if (validationErrors.isEmpty()) {
            if (!hasDuplicates(track)) {
                if(!isOutOfOrder(track)) {
                    trackRepository.addTrack(track);
                    SourceService.processSources(track);
                    return true;
                }
            }
        } else {
            // creating alert
            AlertService.processAlert(track.getSourceId(), track.getTrackNumber(), String.join(", ", validationErrors), "Track validation failure");
        }
        return false;
    }

    // Checking for duplicate tracks
    public static boolean hasDuplicates(Track track){
        listOfTracks = findAllTracks();
        if (listOfTracks != null)
            for (Track oldTrack : listOfTracks){
                if ((oldTrack.getSourceId().equals(track.getSourceId())) &&
                        (oldTrack.getSequenceNumber() == track.getSequenceNumber())){
                    String alertMessage = "Message with the same Source Id and Sequence Number as an existing Track";
                    AlertService.processAlert(track.getSourceId(), track.getTrackNumber(), alertMessage, "Duplicate message");
                    duplicatesCount++;
                    return true;
                }
            }
        return false;
    }

    // storing out of order tracks
    public static boolean isOutOfOrder(Track track){
        Track latestTrack = trackRepository.getLatestTrackFromSource(track.getSourceId());
        if (latestTrack != null)
            if (latestTrack.getSequenceNumber() > track.getSequenceNumber()){
                String alertMessage = "Message with the same Source Id has a higher Sequence Number than an existing Track";
                AlertService.processAlert(track.getSourceId(), track.getTrackNumber(), alertMessage, "Out-of-order message");
                outOfOrderTracks.add(track);
                orderCount++;
                return true;
            }
        return false;
    }

    // getting list of out of order tracks
    public static List<Track> GetOutOfOrderTracks(){
        return outOfOrderTracks;
    }

    public static Track parsingTrackString (String trackString){
        // splitting string into segments
        String[] stringSeg = trackString.split("\\|");
        return new Track(
            stringSeg[2].trim(), //trackNumber
            Double.parseDouble(stringSeg[3].trim()), //latitude
            Double.parseDouble(stringSeg[4].trim()), //longitude
            Double.parseDouble(stringSeg[5].trim()), //speed
            stringSeg[6].trim(), //identity
            stringSeg[7].trim(), //trackStatus
            stringSeg[0].trim(), //sourceId
            Integer.parseInt(stringSeg[1].trim()), //sequenceNumber
            LocalDateTime.parse(stringSeg[8].trim()), //observationTime
            LocalDateTime.now()); //receivedTime
    }

    // Retrieve all tracks
    public static List<Track> findAllTracks() {
        listOfTracks = trackRepository.getAllTracks();
//        System.out.println("\nRetrieving all tracks:");
//        if (listOfTracks != null)
//            for (Track track : listOfTracks) {
//                System.out.println(track);
//            }
//        else
//            System.out.println("No available tracks");
        return listOfTracks;
    }

    // Find tracks by track number
    public static List<Track> findTrackByNum(String trackNumber) {
        listOfTracks = trackRepository.getTrackByNumber(trackNumber);
        System.out.println("\nRetrieving all tracks with track number " + trackNumber + " :");
        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        }
        else
            System.out.println("No available tracks with track number " + trackNumber);
        return listOfTracks;
    }

    // Delete tracks by track number
    public static boolean deleteTrackByNum(String trackNumber) {
        System.out.println("\nDeleting tracks with track number " + trackNumber + " :");
        Boolean trackDeleted =  trackRepository.deleteTrackByNumber(trackNumber);
        if (trackDeleted == true) {
            System.out.println("Track number " + trackNumber + " deleted successfully");
            return true;
        } else {
            System.out.println("Track number " + trackNumber + " not available");
            return false;
        }
    }

    // getting latest tracks from each source
    public List<Track> getCurrentView() {
        System.out.println("\nCurrent View of Tracks: ");
        listOfTracks = trackRepository.getCurrentView();

        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        }
        return listOfTracks;
    }

    //calling get full history view method
    public static List<Track> getFullHistory(String sourceId, String trackNumber) {
        System.out.println("\nFull History of a Track: ");

        listOfTracks = trackRepository.getFullHistory(sourceId, trackNumber);

        if (listOfTracks != null) {
            for (Track track : listOfTracks) {
                System.out.println(track);
            }
        }
        return listOfTracks;
    }

    // Generate processing counter
    public static Map<String, Integer> processingCounter (){
        Map<String, Integer> processCounter = new LinkedHashMap<>();

        // calculating accepted unique messages
        int acceptedCount = 0;
        try{
            acceptedCount  = findAllTracks().size() + orderCount;
        } catch (Exception ignored) {}
        processCounter.put("Accepted Messages", acceptedCount);

        // calculating rejected messages
        int rejectedCount = 0;
        try{
            rejectedCount  = AlertService.getAllAlerts().size();
        } catch (Exception ignored) {

        }
        processCounter.put("Rejected Messages", rejectedCount);

        // calculating received messages
        processCounter.put("Received Messages", acceptedCount + rejectedCount + duplicatesCount);

        // add duplicate count
        processCounter.put("Duplicate Messages", duplicatesCount);

        // calculating processing failure
        processCounter.put("Processing Failure", duplicatesCount + rejectedCount);

        // Displaying counter
        if (processCounter != null) {
            System.out.println("\nDisplaying Processing Count: ");
            for (Map.Entry<String, Integer> entry : processCounter.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }

        return processCounter;
    }

    // Generating filtered tracks
    public static List<Track> getFilteredTracks(String identity, String status, Double minSpeed){
        if (identity.isEmpty())
            identity = null;
        if (status.isEmpty())
            status = null;
        return TrackRepository.filterTracks(identity, status, minSpeed);
    }

//    // Generating cross-source correlation tracks
//    public static List<String> getCorrelatedTracks(){
//        List<String> correlatedTracksInfo = new ArrayList<String>();
//
//        listOfTracks = findAllTracks();
//        // each track compared to other tracks
//        for (Track track : listOfTracks) {
//            for (Track track1 : listOfTracks) {
//                if (track != track1) {
//                    Duration timeDifference = Duration.between(track.getObservationTime(),
//                            track1.getObservationTime());
//                    double speedDifference = Math.abs(track.getSpeed() - track1.getSpeed());
//                    double distanceDifference = calculateDistance(track.getLatitude(), track.getLongitude(),
//                            track1.getLatitude(), track1.getLongitude());
//
//                    // filtering tracks to find matches
//                    // not yet done: fixing time and double results
//                    if(
//                            (track.getTrackNumber().equals(track1.getTrackNumber())) &&
//                            (!track.getSourceId().equals(track1.getSourceId())) &&
//                            (track.getIdentity().equals(track1.getIdentity())) &&
//                            (speedDifference < 100) &&
//                            (distanceDifference < 2) &&
//                            ((timeDifference.compareTo(Duration.ofSeconds(30)) <= 0) && timeDifference.getSeconds()>=0)
//                    ){
//                        correlatedTracksInfo.add(
//                                "\nTracks SourceId: " + track.getSourceId() + ", " + track1.getSourceId() +
//                                "\nTrack Number: " + track.getTrackNumber() +
//                                "\nFirst Track Position: " + track.getLatitude() + ", " + track.getLongitude() +
//                                "\nSecond Track Position: " + track1.getLatitude() + ", " + track1.getLongitude() +
//                                "\nTime Difference: " + timeDifference.getSeconds()+ " seconds" +
//                                "\nSpeed Difference: " + speedDifference +
//                                "\nDistance: " + distanceDifference + " km"
//                        );
//                    }
//                }
//            }
//        }
//        System.out.println("Displaying matching tracks");
//        for(String matches : correlatedTracksInfo)
//            System.out.println(matches);
//
//        return correlatedTracksInfo;
//    }
//
//    // calculation to find the distance between two points
//    public static double calculateDistance(double latitude1, double longtitude1, double latitude2, double longtitude2){
//        double radLatitude1 = Math.toRadians(latitude1);
//        double radLongtitude1 = Math.toRadians(longtitude1);
//        double radLatitude2 = Math.toRadians(latitude2);
//        double radLongtitude2 = Math.toRadians(longtitude2);
//        double xAxis = (radLongtitude2 - radLongtitude1) * Math.cos((radLatitude1 + radLatitude2)/2);
//        double yAxis = (radLatitude2 - radLatitude1);
//        return Math.sqrt(xAxis*xAxis + yAxis*yAxis) * 6371; //earth radius
//    }

}