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

    public TrackController(TrackService tracks){
        //this.reads=reads;
        this.tracks=tracks;
    }

    // retuen all observations as JSON format
    @GetMapping("/list")
    public List<Track> getAllTracks(){
        return tracks.displayTracks();
    }

    // Get /api/tracks/search?trackNumber=###
    @GetMapping("/search/{trackNumber}")
    public List<Track> searchTrackNumber(@PathVariable String trackNumber){
        return tracks.getTracksByNumber(trackNumber);
    }

    @GetMapping("/filter/{identity}/{status}/{minSpeed}")
    public List<Track> filterTrackks(@PathVariable String identity, @PathVariable String status, @PathVariable String minSpeed){
        List<String> filters = Arrays.asList(
                "identity_type = ",
                "track_status = ",
                "speed >= "
        );
        List<String> values = Arrays.asList(
                "'" + identity +"'",
                "'"+status+"'",
                "'"+minSpeed+"'"
        );
        return tracks.filterTracks(filters, values, null, null, null);
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
        return tracks.getTracksByNumber(track_number);
    }

    @GetMapping(value = "/history", params = "!search")
    public ModelAndView showHistoryForm(){
        return new ModelAndView("forward:/search-trackNo-form.html");
    }

    @GetMapping(value = "/history", params = "search")
    public List<Track> searchHistoryTracks(@RequestParam("source_id") String source_id, @RequestParam("track_number") String track_number){
        return tracks.getAllTrackHistory(track_number, source_id);
    }


    @GetMapping("/filter")
    public List<Track> filterTracks(@ModelAttribute FilterParameters filterParams) {
        List<String> filters = new ArrayList<>();
        List<String> filter_values = new ArrayList<>();
        List<String> sort = new ArrayList<>();
        String input_value;
        LocalDateTime start_time = null;
        LocalDateTime end_time = null;

        try {
            if(!filterParams.getSourceId().equals("")) {
                filters.add("sourceId = ");
                filter_values.add("'" + filterParams.getSourceId() + "'");
            }
            if(!filterParams.getTrackNumber().equals("")) {
                filters.add("trackNumber = ");
                filter_values.add("'" + filterParams.getTrackNumber() + "'");
            }
            if(!filterParams.getIdentity().equals("")) {
                filters.add("identity = ");
                filter_values.add("'" + filterParams.getIdentity() + "'");
            }
            if(!filterParams.getStatus().equals("")) {
                filters.add("status = ");
                filter_values.add("'" + filterParams.getStatus() + "'");
            }
            if(!filterParams.getMinSpeed().equals("")) {
                filters.add("speed >= ");
                filter_values.add(filterParams.getMinSpeed() + "'");
            }
            if(!filterParams.getMaxSpeed().equals("")) {
                filters.add("speed <= ");
                filter_values.add(filterParams.getMaxSpeed() + "'");
            }

            input_value = filterParams.getObservationType();
            if (!input_value.equals("")) {
                if (input_value.equals("current")) {
                    start_time = LocalDateTime.now().minusMinutes(5);
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                } else {
                    start_time = LocalDateTime.of(2025, 6, 1, 0, 0, 0);
                    end_time = LocalDateTime.now().minusMinutes(5);
                    filters.add("created_time");
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                    filter_values.add(" <= ?");
                }
            } else {
                input_value = filterParams.getStartTime();
                if (input_value != null && !input_value.equals("")) {
                    start_time = LocalDateTime.parse(input_value);
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                }
                input_value = filterParams.getEndTime();
                if (input_value != null && !input_value.equals("")) {
                    end_time = LocalDateTime.parse(input_value);
                    filters.add("created_time");
                    filter_values.add(" <= ?");
                }
            }

            if (!filterParams.getTimeSort().equals("")) {
                if (filterParams.getTimeSortDirection().equals("DESC"))
                    sort.add("created_time DESC");
                else
                    sort.add("created_time");
            }

            if (!filterParams.getSpeedSort().equals("")) {
                if (filterParams.getSpeedSortDirection().equals("DESC"))
                    sort.add("speed DESC");
                else
                    sort.add("speed");
            }

            if (!filterParams.getTrackNumber().equals("")) {
                if (filterParams.getTrackNumberSortDirection().equals("DESC"))
                    sort.add("track_number DESC");
                else
                    sort.add("track_number");
            }

            return tracks.filterTracks(filters, filter_values, sort, start_time, end_time);
        }catch (Exception e) {
//            // Set the HTTP response status to 500 (Internal Server Error)
//            response.setStatus(500);
//            // Display an error message if retrieval fails
//            writer.println("<p>Unable to get Statistics.</p>");
        }

        return null;
    }

    @GetMapping(value = "/maintenance")
    public ModelAndView showDeleteForm(){
        return new ModelAndView("forward:/delete-form.html");
    }

    @PostMapping(value = "/maintenance")
    public boolean deleteTrack(@RequestParam("track_number") String track_number){
        return tracks.deleteTrack(track_number);
    }

}
