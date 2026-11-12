package com.mtms.controller;

import com.mtms.domain.ReplaySpeed;
import com.mtms.domain.Track;
import com.mtms.service.ReplayService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FormController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private ReplayService replayService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/api/tracks", params = {"trackNumber", "latitude", "longitude",
            "speed", "identity", "trackStatus", "sourceId", "sequenceNumber", "observationTime"})
    public String addTrack(@RequestParam("trackNumber") String trackNumber,
                           @RequestParam("latitude") double latitude,
                           @RequestParam("longitude") double longitude,
                           @RequestParam("speed") double speed,
                           @RequestParam("identity") String identity,
                           @RequestParam("trackStatus") String status,
                           @RequestParam("sourceId") String sourceId,
                           @RequestParam("sequenceNumber") int sequenceNumber,
                           @RequestParam("observationTime") LocalDateTime observationTime){


        Track track = new Track(trackNumber, latitude, longitude, speed, identity,
                status, sourceId, sequenceNumber, observationTime, LocalDateTime.now());

        StringBuilder result = new StringBuilder();
        result.append("========== CREATING TRACK ==========<pre>");
        if(trackService.processTrack(track))
            result.append("Track ").append(track.getTrackNumber()).append(" has been added.");
        else
            result.append("Track ").append(track.getTrackNumber()).append(" could not be added.");
        return result.toString();
    }


    @ResponseBody
    @GetMapping(value="/api/tracks/replay", params = {"startTime", "endTime", "speed"})
    public String getFilteredTracks(@RequestParam("startTime") LocalDateTime startTime,
                                    @RequestParam("endTime") LocalDateTime endTime,
                                    @RequestParam("speed") String speed){

        //converting user input to enum
        ReplaySpeed speedEnum;
        switch (speed) {
            case "half":
                speedEnum = ReplaySpeed.HALF;
            case "double":
                speedEnum = ReplaySpeed.DOUBLE;
            default:
                speedEnum = ReplaySpeed.NORMAL;
        };

        List<Track> listOfTracks = replayService.replayTracks(speedEnum, startTime, endTime);

        StringBuilder result = new StringBuilder();

        if (listOfTracks != null && !listOfTracks.isEmpty()) {

            result.append("========== REPLAY TRACK RESULTS ==========<pre>");
            result.append("Displaying tracks with ").append(speedEnum).append(" speed\n");
            result.append("------------------------------<pre>");

            buildingString(listOfTracks, result);

        } else {

            result.append("Tracks could not be found.");
        }

        return result.toString();
    }

    @ResponseBody
    @GetMapping("/api/tracks/current")
    public String getCurrentTracks() {
        List<Track> listOfTracks = trackService.getCurrentView();

        StringBuilder result = new StringBuilder();

        result.append("========== LIST OF CURRENT TRACKS ==========<pre>");

        if (listOfTracks == null || listOfTracks.isEmpty()) {

            result.append("No tracks available.");

        } else {

            buildingString(listOfTracks, result);
        }
        return result.toString();
    }

    @ResponseBody
    @GetMapping("/api/tracks/list")
    public String getTracks(Model model) {
        model.addAttribute("tracks", "Tracks List");
        List<Track> listOfTracks = trackService.findAllTracks();

        StringBuilder result = new StringBuilder();

        result.append("========== LIST OF ALL TRACKS ==========<pre>");

        if (listOfTracks == null || listOfTracks.isEmpty()) {

            result.append("No tracks available.");

        } else {

            buildingString(listOfTracks, result);
        }
        return result.toString();
    }

    //localhost:8080/api/tracks/filter?identity=FRIEND&status=ACTIVE&minSpeed=450
    @ResponseBody
    @GetMapping(value="/api/tracks/filter", params = {"identity", "status", "minimumSpeed"})
    public String getFilteredTracks(@RequestParam("identity") String identity,
                                    @RequestParam("status") String status,
                                    @RequestParam("minimumSpeed") String minimumSpeed){
        List<Track> listOfTracks;
        if (!minimumSpeed.isEmpty())
            listOfTracks =  trackService.getFilteredTracks(identity, status, Double.parseDouble(minimumSpeed));
        else
            listOfTracks =  trackService.getFilteredTracks(identity, status, null);

        StringBuilder result = new StringBuilder();

        if (listOfTracks != null && !listOfTracks.isEmpty()) {

            result.append("========== FILTERED TRACK RESULTS ==========<pre>");

            buildingString(listOfTracks, result);

        } else {

            result.append("Tracks could not be found.");
        }

        return result.toString();
    }

    @ResponseBody
    @GetMapping(value="/api/tracks/advancedFilter", params = {"sourceId", "trackNumber", "identity",  "status",
                                                                "minSpeed", "maxSpeed", "startTime", "endTime"})
    public String getAdvanceFilteredTracks(@RequestParam("sourceId") String sourceId,
                                           @RequestParam("trackNumber") String trackNumber,
                                           @RequestParam("identity") String identity,
                                           @RequestParam("status") String status,
                                           @RequestParam("minSpeed") String minSpeed,
                                           @RequestParam("maxSpeed") String maxSpeed,
                                           @RequestParam("startTime") String startTime,
                                           @RequestParam("endTime") String endTime){

        List<Track> listOfTracks = trackService.getAdvancedFilteredTracks(sourceId, trackNumber,
                identity, status, minSpeed, maxSpeed, startTime, endTime);

        StringBuilder result = new StringBuilder();

        if (listOfTracks != null && !listOfTracks.isEmpty()) {

            result.append("========== ADVANCED FILTERED TRACK RESULTS ==========<pre>");

            buildingString(listOfTracks, result);

        } else {

            result.append("Tracks could not be found.");
        }

        return result.toString();
    }


    @ResponseBody
    @GetMapping(value = "/api/tracks/history", params = {"sourceId", "trackNumber"})
    public String getFullHistory(@RequestParam("sourceId") String sourceId,
                                      @RequestParam("trackNumber") String trackNumber){

        List<Track> listOfTracks = trackService.getFullHistory(trackNumber, sourceId);

        StringBuilder result = new StringBuilder();

        if (listOfTracks != null && !listOfTracks.isEmpty()) {

            result.append("========== TRACK HISTORY RESULTS ==========<pre>");

            buildingString(listOfTracks, result);

        } else {

            result.append("Tracks could not be found.");
        }

        return result.toString();
    }

    @ResponseBody
    @GetMapping("/api/tracks/search")
    public String searchTracks(@RequestParam("trackNumber") String trackNumber,
                               Model model) {
        model.addAttribute("tracks", "Tracks Search");
        List<Track> listOfTracks = trackService.findTrackByNum(trackNumber);

        StringBuilder result = new StringBuilder();

        if (listOfTracks != null && !listOfTracks.isEmpty()) {

            result.append("========== TRACK SEARCH RESULTS ==========<pre>");
            result.append("Displaying tracks with track number: ")
                    .append(trackNumber).append("\n");

            buildingString(listOfTracks, result);

        } else {

            result.append("Tracks with track number ")
                    .append(trackNumber)
                    .append(" could not be found.");
        }

        return result.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/api/tracks/maintenance", params = "trackNumber")
    public String deleteTrackByNum(@RequestParam("trackNumber") String trackNumber){
        StringBuilder result = new StringBuilder();
        result.append("========== DELETING TRACKS ==========<pre>");
        if(trackService.deleteTrackByNum(trackNumber))
            result.append("Track ").append(trackNumber).append(" has been deleted.");
        else
            result.append("Track ").append(trackNumber).append(" could not be deleted.");
        return result.toString();
    }

    public void buildingString(List<Track> listOfTracks, StringBuilder result){
        for (Track track : listOfTracks) {
            result.append("Track ID: ").append(track.getTrackId()).append("\n");
            result.append("Track Number: ").append(track.getTrackNumber()).append("\n");
            result.append("Latitude: ").append(track.getLatitude()).append("\n");
            result.append("Longitude: ").append(track.getLongitude()).append("\n");
            result.append("Speed: ").append(track.getSpeed()).append("\n");
            result.append("Identity: ").append(track.getIdentity()).append("\n");
            result.append("Track Status: ").append(track.getTrackStatus()).append("\n");
            result.append("Source ID: ").append(track.getSourceId()).append("\n");
            result.append("Sequence Number: ").append(track.getSequenceNumber()).append("\n");
            result.append("Observation Time: ").append(track.getObservationTime()).append("\n");
            result.append("Received Time: ").append(track.getReceivedTime()).append("\n");
            result.append("------------------------------<pre>");
        }
    }
}
