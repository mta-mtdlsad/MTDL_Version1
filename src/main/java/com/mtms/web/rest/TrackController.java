package com.mtms.web.rest;

import com.mtms.domain.Track;
import com.mtms.service.TrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {
    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/index.html";
    }

    @GetMapping("/list")
    public List<Track> listTracks() {
        return trackService.getAllTracks();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchTracks(@RequestParam("trackNumber") String trackNumber) {
        if (trackNumber == null || trackNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Track> results = trackService.searchTracks(trackNumber.trim());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Track>> filterTracks(
            @RequestParam(value = "category", required = false) String identity,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "minimumSpeed", required = false) Double minSpeed) {

        List<Track> filteredResults = trackService.filterTracks(identity, status, minSpeed);
        return ResponseEntity.ok(filteredResults);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTrackHistory(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        if (start == null || start.trim().isEmpty() || end == null || end.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start and end timestamps are required."));
        }

        try {
            List<Track> historyResults = trackService.getTrackHistory(start, end);
            return ResponseEntity.ok(historyResults);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format: " + e.getMessage()));
        }
    }


    @PostMapping("/create")
    public ResponseEntity<String> createTrack(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("sequenceNumber") Integer sequenceNumber,
            @RequestParam("trackNumber") String trackNumber,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("speed") double speed,
            @RequestParam("category") String category,
            @RequestParam("trackStatus") String trackStatus) {

        Track track = new Track();
        track.setSourceId(sourceId.trim());
        track.setSequenceNumber(sequenceNumber);
        track.setTrackNumber(trackNumber.trim());
        track.setLatitude(latitude);
        track.setLongitude(longitude);
        track.setSpeed(speed);
        track.setIdentityType(category.trim());
        track.setTrackStatus(trackStatus.trim());
        track.setCreatedTime(LocalDateTime.now());

        trackService.processTrack(track);
        return ResponseEntity.ok("Track " + track.getTrackNumber() + " successfully submitted for processing and storage.");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteTrack(@RequestParam("trackNumber") String trackNumber) {
        if (trackNumber == null || trackNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing required 'trackNumber' target deletion parameter.");
        }
        int deletedRows = trackService.deleteTracksByNumber(trackNumber.trim());
        if (deletedRows > 0) {
            return ResponseEntity.ok("Successfully removed " + deletedRows + " record(s) matching '" + trackNumber + "'.");
        } else {
            return ResponseEntity.status(404).body("No database rows matched '" + trackNumber + "'.");
        }
    }

    @GetMapping("/current")
    public List<Track> getCurrentTracks(){
        return trackService.getLatestTracksView();
    }
}
