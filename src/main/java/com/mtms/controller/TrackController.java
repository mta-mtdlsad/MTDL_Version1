package com.mtms.controller;
import com.mtms.model.FilterParameters;
import com.mtms.model.Track;
import com.mtms.service.TrackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    @Autowired
    private TrackService tracks;


    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public Track addTrack(@RequestBody Track track){
        return tracks.createTrackFromApi(track);
        //return tracks.saveTrack(track);
    }

    // retuen all observations as JSON format
    @GetMapping("/list")
    public List<Track> getAllTracks(){
        return tracks.getAllTracks();
    }

    // Get /api/tracks/search?trackNumber=###
    @GetMapping("/search/{trackNumber}")
    public List<Track> searchTrackNumber(@PathVariable String trackNumber){
        return tracks.findTracksByNumber(trackNumber);
    }

    @GetMapping("/filter/{identity}/{status}/{minSpeed}")
    public List<Track> filterTracks(@PathVariable String identity, @PathVariable String status, @PathVariable String minSpeed){
        FilterParameters filterParams = new FilterParameters();
        filterParams.setIdentity(identity);
        filterParams.setStatus(status);
        filterParams.setMinSpeed(minSpeed);
        return tracks.filterTracks(filterParams);
    }

    @GetMapping("/history/{source_id}/{track_number}")
    public List<Track> filterTracks(@PathVariable String source_id, @PathVariable String track_number){
        return tracks.getAllTrackHistory(track_number, source_id);
    }


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


    @GetMapping("/filter")
    public List<Track> filterTracks(@ModelAttribute FilterParameters filterParams) {
        return tracks.filterTracks(filterParams);
    }

    @GetMapping(value = "/maintenance")
    public ModelAndView showDeleteForm(){
        return new ModelAndView("forward:/delete-form.html");
    }

    @PostMapping(value = "/maintenance")
    public boolean deleteTrack(@RequestParam("track_number") String track_number){
        return tracks.deleteTracksByTrackNumber(track_number);
    }

}
