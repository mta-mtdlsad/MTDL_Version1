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
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    @Autowired
    private TrackService tracks;

    @Autowired
    private MessageProcessorService processor;

    @Autowired
    private SourceHealthService sourceHealth;

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
        return tracks.getAllTracks();
    }

    // Get /api/tracks/search?trackNumber=###
    @GetMapping("/search/{trackNumber}")
    public List<Track> searchTrackNumber(@PathVariable String trackNumber){
        return tracks.findTracksByNumber(trackNumber);
    }

    // Filters tracks based on identity, status, and minSpeed
    @GetMapping("/filter/{identity}/{status}/{minSpeed}")
    public List<Track> filterTracks(@PathVariable String identity, @PathVariable String status, @PathVariable String minSpeed){
        FilterParameters filterParams = new FilterParameters();
        filterParams.setIdentity(identity);
        filterParams.setStatus(status);
        filterParams.setMinSpeed(minSpeed);
        return tracks.filterTracks(filterParams);
    }

    // Filters tracks dynamically based form
    @GetMapping("/filter")
    public List<Track> filterTracks(@ModelAttribute FilterParameters filterParams) {
        return tracks.filterTracks(filterParams);
    }

    // Get a track's history based on a sourceId and trackNumber
    @GetMapping("/history/{source_id}/{track_number}")
    public List<Track> getHistory(@PathVariable String source_id, @PathVariable String track_number){
        return tracks.getAllTrackHistory(track_number, source_id);
    }

    // Deletes based on trackNumber
    @PostMapping(value = "/maintenance")
    public boolean deleteTrack(@RequestParam("track_number") String track_number){
        return tracks.deleteTracksByTrackNumber(track_number);
    }


    // !!! Functions below deal with redirecting so HTML pages are functional !!!

    @GetMapping(value = "/search", params = "!track_number")
    public ModelAndView showSearchForm(){
        return new ModelAndView("forward:/search-form.html");
    }

    @GetMapping(value = "/search", params = "track_number")
    public List<Track> searchTracks(@RequestParam("track_number") String track_number){
        return tracks.findTracksByNumber(track_number);
    }
/*
    @GetMapping(value = "/history", params = "!search")
    public ModelAndView showHistoryForm(){
        return new ModelAndView("forward:/search-trackNo-form.html");
    }*/

    @GetMapping(value = "/history") //, params = "search")
    public List<Track> searchHistoryTracks(@RequestParam("source_id") String source_id, @RequestParam("track_number") String track_number){
        return tracks.getAllTrackHistory(track_number, source_id);
    }

    @GetMapping(value = "/maintenance")
    public ModelAndView showDeleteForm(){
        return new ModelAndView("forward:/delete-form.html");
    }

}
