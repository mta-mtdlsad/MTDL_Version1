package com.mtms.controller;
import com.mtms.util.FilterParameters;
import com.mtms.model.Track;
import com.mtms.service.MessageProcessorService;
import com.mtms.service.SourceHealthService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping(value = "/search", params = "track_number")
    public List<Track> searchTracks(@RequestParam("track_number") String track_number){
        return trackService.findTracksByNumber(track_number);
    }

    // Filters tracks based on identity, status, and minSpeed
    @GetMapping("/filter/{identity}/{status}/{minSpeed}")
    public List<Track> filterTracks(@PathVariable String identity, @PathVariable String status, @PathVariable String minSpeed){
        FilterParameters filterParams = new FilterParameters();
        filterParams.setIdentity(identity);
        filterParams.setStatus(status);
        filterParams.setMinSpeed(minSpeed);
        return trackService.filterTracks(filterParams);
    }

    // Filters tracks dynamically based form
    @GetMapping("/filter")
    public List<Track> filterTracks(@ModelAttribute FilterParameters filterParams) {
        return trackService.filterTracks(filterParams);
    }

    @GetMapping(value = "/history") //, params = "search")
    public List<Track> searchHistoryTracks(@RequestParam("source_id") String source_id, @RequestParam("track_number") String track_number){
        return trackService.getAllTrackHistory(track_number, source_id);
    }

    // Deletes based on trackNumber
    @PostMapping(value = "/maintenance")
    public boolean deleteTrack(@RequestParam("track_number") String track_number){
        return trackService.deleteTracksByTrackNumber(track_number);
    }


    // !!! Functions below deal with redirecting so HTML pages are functional !!!

    @PostMapping(value = "/createTrack")
    public ResponseEntity<?> createTrackFromForm(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("sequenceNumber") int sequenceNumber,
            @RequestParam("trackNumber") String trackNumber,
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

    @GetMapping(value = "/search", params = "!track_number")
    public ModelAndView showSearchForm(){
        return new ModelAndView("forward:/search-form.html");
    }
/*
    @GetMapping(value = "/history", params = "!search")
    public ModelAndView showHistoryForm(){
        return new ModelAndView("forward:/search-trackNo-form.html");
    }*/

    @GetMapping(value = "/maintenance")
    public ModelAndView showDeleteForm(){
        return new ModelAndView("forward:/delete-form.html");
    }

}
