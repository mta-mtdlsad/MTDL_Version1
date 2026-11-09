package com.mtms.controller;

import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FormController {

    private TrackService trackService;

    @ResponseBody
    @GetMapping("/api/tracks/list")
    public String getTracks(Model model) {
        model.addAttribute("tracks", "Tracks List");
        List<Track> listOfTracks = TrackService.findAllTracks();

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
                                         @RequestParam("minimumSpeed") String minimumSpeed,
                                         Model model){
        List<Track> listOfTracks;
        if (!minimumSpeed.isEmpty())
            listOfTracks =  TrackService.getFilteredTracks(identity, status, Double.parseDouble(minimumSpeed));
        else
            listOfTracks =  TrackService.getFilteredTracks(identity, status, null);

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
    @GetMapping(value = "/api/tracks/history", params = {"sourceId", "trackNumber"})
    public String getFullHistory(@RequestParam("sourceId") String sourceId,
                                      @RequestParam("trackNumber") String trackNumber){

        List<Track> listOfTracks = TrackService.getFullHistory(sourceId, trackNumber);

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
        List<Track> listOfTracks = TrackService.findTrackByNum(trackNumber);

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
        result.append("========== DELETING TRACKS RESULTS ==========<pre>");
        if(TrackService.deleteTrackByNum(trackNumber))
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
