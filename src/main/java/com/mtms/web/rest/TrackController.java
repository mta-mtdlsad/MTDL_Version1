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

    /*
     * Validates, deduplicates, and saves a JSON Track payload
     *      via MessageProcessorService (POST /api/tracks/create).
     *
     * @param track The Track entity from the JSON request body.
     * @return 201 Created (ACCEPTED), 422 Unprocessable Entity (REJECTED),
     *      409 Conflict (DUPLICATE), or 500 Internal Server Error (FAILURE) with the ProcessingResult body.
     */
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

    /*
     * Retrieves all stored track observations (GET /api/tracks/list).
     *
     * @return A list of all Track entities.
     */
    @GetMapping("/list")
    public List<Track> getAllTracks(){
        return trackService.getAllTracks();
    }

    /*
     * Searches for all track observations matching a
     *      specific track number (GET /api/tracks/search).
     *
     * @param trackNumber The track number query parameter to search for.
     * @return 200 OK with matching tracks, or 400 Bad Request if trackNumber is blank.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Track>> searchTracks(@RequestParam("trackNumber") String trackNumber) {
        if (trackNumber == null || trackNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Track> results = trackService.findTracksByNumber(trackNumber.trim());
        return ResponseEntity.ok(results);
    }

    /*
     * Filters tracks dynamically by identity category,
     *      operational status, and minimum speed (GET /api/tracks/filter).
     *
     * @param identity Optional identity type filter (e.g., FRIEND, HOSTILE).
     * @param status   Optional track status filter (e.g., ACTIVE, INACTIVE).
     * @param minSpeed Optional minimum speed threshold string.
     * @return 200 OK containing the filtered list of Track entities.
     */
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

    /*
     * Retrieves historical track observations within a start
     *      and end timestamp window, optionally filtered by
     *      track number (GET /api/tracks/history).
     *
     * @param start       Start timestamp string (required).
     * @param end         End timestamp string (required).
     * @param trackNumber Optional track number filter.
     * @return 200 OK with matching historical tracks, or
     *      400 Bad Request if timestamps are missing or malformed.
     */
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

    /*
     * Deletes all track records matching the provided track number (POST /api/tracks/delete).
     *
     * @param trackNumber The track number of records to delete.
     * @return 200 OK if deleted, 400 Bad Request if parameter is missing,
     *      or 404 Not Found if no records matched.
     */
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

    /*
     * Creates and processes a new Track submitted via HTML form
     *      parameters (POST /api/tracks/createTrack).
     *
     * @param sourceId         Identifier of the reporting source.
     * @param sequenceNumber   Message sequence number.
     * @param trackNumber      Unique track number identifier.
     * @param trackType        Platform type (e.g., AIR, LAND, SEA).
     * @param trackLatitude    Latitude in decimal degrees.
     * @param trackLongitude   Longitude in decimal degrees.
     * @param trackSpeed       Speed value.
     * @param trackIdentity    Identity classification (FRIEND, HOSTILE, NEUTRAL, UNKNOWN).
     * @param trackStatus      Operational status (ACTIVE, INACTIVE).
     * @param trackCreatedTime ISO-8601 date-time string from HTML datetime-local input.
     * @return 201 Created, 422 Unprocessable Entity, 409 Conflict, or
     *      500 Internal Server Error with a plain-text status message.
     */
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

    /*
     * Retrieves only the most recent observation for each
     *      unique (sourceId, trackNumber) pair (GET /api/tracks/current).
     *
     * @return A list of the latest Track entities.
     */
    @GetMapping("/current")
    public List<Track> getLatestTracks() {
        return trackService.getLatestTracksView();
    }

    /*
     * Calculates the nearest neighboring track for each current
     *      track (or a specific trackId if provided) (GET /api/tracks/nearest-unique).
     *
     * @param trackId Optional track ID query parameter to filter results to a single track.
     * @return 200 OK containing a list of Maps detailing
     *      each track pair and their separation distance in kilometers.
     */
    @GetMapping("/nearest-unique")
    public ResponseEntity<List<Map<String, Object>>> getNearestUniqueTracks(@RequestParam(required = false) String trackId) {
        return ResponseEntity.ok(trackService.getNearestUniqueTracks(trackId));
    }
}