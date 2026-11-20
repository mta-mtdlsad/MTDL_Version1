package com.mtms.web.rest;
import com.mtms.util.FilterParameters;
import com.mtms.model.Track;
import com.mtms.service.MessageProcessorService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private MessageProcessorService processor;

    // Adds track using MessageProcessorService then returns ResponseEntity based on ProcessingResult
    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<?>  addTrack(@RequestBody Track track){
        //1. validate and process the track
        MessageProcessorService.ProcessingResult result = processor.processDetailed(track);

        //2. return the processing result
        return switch (result.outcome()) {
            case ACCEPTED -> ResponseEntity.status(201).body(result);
            case REJECTED -> ResponseEntity.status(422).body(result);
            case DUPLICATE -> ResponseEntity.status(409).body(result);
            case FAILURE -> ResponseEntity.status(500).body(result);
        };
    }

    // Returns all observations as JSON format
    @GetMapping("/list")
    public List<Track> getAllTracks(){
        return trackService.getAllTracks();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchTracks(@RequestParam("trackNumber") String trackNumber) {
        if (trackNumber == null || trackNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Track> results = trackService.findTracksByNumber(trackNumber.trim());
        return ResponseEntity.ok(results);
    }

    // Filters tracks based on identity, status, and minSpeed
    @GetMapping("/filter")
    public ResponseEntity<List<Track>> filterTracks(
            @RequestParam(value = "category", required = false) String identity,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "minimumSpeed", required = false) String minSpeed) {

        FilterParameters filterParams = new FilterParameters();
        filterParams.setIdentity(identity);
        filterParams.setStatus(status);
        filterParams.setMinSpeed(minSpeed);

        List<Track> filteredResults = trackService.filterTracks(filterParams);
        return ResponseEntity.ok(filteredResults);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTrackHistory(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "trackNumber", required = false) String trackNumber) {

        if (start == null || start.trim().isEmpty() || end == null || end.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start and end timestamps are required."));
        }

        try {
            List<Track> historyResults = trackService.getTrackHistory(start, end, trackNumber);
            return ResponseEntity.ok(historyResults);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format: " + e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteTrack(@RequestParam("trackNumber") String trackNumber) {
        if (trackNumber == null || trackNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing required 'trackNumber' target deletion parameter.");
        }
        boolean deletedRows = trackService.deleteTracksByTrackNumber(trackNumber.trim());
        if (deletedRows) {
            return ResponseEntity.ok("Successfully removed " + deletedRows + " record(s) matching '" + trackNumber + "'.");
        } else {
            return ResponseEntity.status(404).body("No database rows matched '" + trackNumber + "'.");
        }
    }

    @PostMapping(value = "/createTrack")
    public ResponseEntity<?> createTrackFromForm(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("sequenceNumber") int sequenceNumber,
            @RequestParam("trackNumber") String trackNumber,
            @RequestParam("trackType") String trackType,
            @RequestParam("trackLatitude") double trackLatitude,
            @RequestParam("trackLongitude") double trackLongitude,
            @RequestParam("trackSpeed") double trackSpeed,
            @RequestParam("trackIdentity") String trackIdentity,
            @RequestParam("trackStatus") String trackStatus,
            @RequestParam("trackCreatedTime") String trackCreatedTime
    ) {
        // Convert HTML datetime-local value to LocalDateTime
        LocalDateTime createdTime = LocalDateTime.parse(trackCreatedTime);
        // Create a Track using your existing constructor

        Track track = new Track(
                sourceId,
                sequenceNumber,
                trackNumber,
                trackType,
                trackLatitude,
                trackLongitude,
                trackSpeed,
                trackIdentity,
                trackStatus,
                createdTime
        );

        // Use the same validation and processing as Postman
        MessageProcessorService.ProcessingResult result = processor.processDetailed(track);
        // Return the result to the browser
        return switch (result.outcome()) {
            case ACCEPTED -> ResponseEntity.status(201)
                    .body("Track created successfully!");
            case REJECTED -> ResponseEntity.status(422)
                    .body("Track rejected: invalid data.");
            case DUPLICATE -> ResponseEntity.status(409)
                    .body("Duplicate source sequence number.");
            case FAILURE -> ResponseEntity.status(500)
                    .body("Track processing failed.");
        };
    }

    @GetMapping("/current")
    public List<Track> getLatestTracks() {
        return trackService.getLatestTracksView();
    }

    @GetMapping("/nearest-unique")
    public ResponseEntity<List<Map<String, Object>>> getNearestUniqueTracks(@RequestParam(required = false) String trackId) {
        return ResponseEntity.ok(trackService.getNearestUniqueTracks(trackId));
    }
}