package com.mtms.controller;
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
    public List<Track> filterTracks(@RequestParam("observationType") String observationType,
                                    @RequestParam("startTime") String startTime,
                                    @RequestParam("endTime") String endTime,
                                    @RequestParam("timeSort") String timeSort,
                                    @RequestParam("timeSortDirection") String timeSortDirection,
                                    @RequestParam("speedSort") String speedSort,
                                    @RequestParam("speedSortDirection") String speedSortDirection,
                                    @RequestParam("trackNumberSort") String trackNumberSort,
                                    @RequestParam("trackNumberSortDirection") String trackNumberSortDirection,
                                    HttpServletRequest request) {
        Map<String, String> filterParameters = Map.of(
                "sourceId", "source_id = ",
                "trackNumber", "track_number = ",
                "identity", "identity_type = ",
                "status", "track_status = ",
                "minSpeed", "speed >= ",
                "maxSpeed", "speed <= ");

        List<String> filters = new ArrayList<>();
        List<String> filter_values = new ArrayList<>();
        List<String> sort = new ArrayList<>();
        String input_value;
        LocalDateTime start_time = null;
        LocalDateTime end_time = null;

        try {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                input_value = request.getParameter(entry.getKey());
                if (!input_value.isEmpty()) {
                    filters.add(entry.getValue());
                    if (!entry.getKey().equals("minSpeed") && !entry.getKey().equals("maxSpeed"))
                        input_value = "'" + input_value + "'";
                    filter_values.add(input_value);
                }
            }

            input_value = observationType;
            if (!input_value.isEmpty()) {
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
                input_value = startTime;
                if (input_value != null && !input_value.isBlank()) {
                    start_time = LocalDateTime.parse(input_value);
                    filters.add("created_time");
                    filter_values.add(" >= ?");
                }
                input_value = endTime;
                if (input_value != null && !input_value.isBlank()) {
                    end_time = LocalDateTime.parse(input_value);
                    filters.add("created_time");
                    filter_values.add(" <= ?");
                }
            }

            if (timeSort != null) {
                if (timeSortDirection.equals("DESC"))
                    sort.add("created_time DESC");
                else
                    sort.add("created_time");
            }

            if (speedSort != null) {
                if (speedSortDirection.equals("DESC"))
                    sort.add("speed DESC");
                else
                    sort.add("speed");
            }

            if (trackNumberSort != null) {
                if (trackNumberSortDirection.equals("DESC"))
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
