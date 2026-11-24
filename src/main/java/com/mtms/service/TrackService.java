package com.mtms.service;

import com.mtms.util.FilterParameters;
import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import com.mtms.util.TrackSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrackService {

    @Autowired
    private TrackRepository track_repo;

    /*
     * Saves a Track to the database only if no record with
     *      the same sourceId and sequenceNumber already exists.
     *
     * @param track The Track entity to save.
     * @return true if saved successfully, false if it was a duplicate.
     */
    public boolean saveTrack(Track track) {
        boolean exists = track_repo.existsBySourceIdAndSequenceNumber(track.getSourceId(), track.getSequenceNumber());
        if (exists) {
            return false;
        } else {
            track_repo.save(track);
            return true;
        }
    }

    /*
     * Retrieves all Track entities from the database.
     *
     * @return A list of all Track entities.
     */
    public List<Track> getAllTracks() {
        List<Track> listOfTracks = new ArrayList<>();
        track_repo.findAll().forEach(listOfTracks::add);
        return listOfTracks;

    }

    /*
     * Finds all track observations matching a specific track number.
     *
     * @param number The track number string to search for.
     * @return A list of matching Track entities.
     */
    public List<Track> findTracksByNumber(String number) {
        return track_repo.findTracksByTrackNumber(number);
    }

    /*
     * Deletes all track records matching a specific track number.
     *
     * @param number The track number string of records to delete.
     * @return true if one or more records were deleted, false otherwise.
     */
    @Transactional
    public Boolean deleteTracksByTrackNumber(String number) {
        long deleted = track_repo.deleteAllByTrackNumber(number);
        return deleted > 0;
    }

    /*
     * Finds the highest sequence number recorded for a given source ID.
     *
     * @param sourceId The unique source identifier.
     * @return The highest sequence number found, or 0 if no tracks exist for that source.
     */
    public int getHighestSequence(String sourceId) {
        List<Track> listOfTracks = track_repo.findTracksBySourceId(sourceId);
        return listOfTracks.stream().mapToInt(Track::getSequenceNumber).max().orElse(0);
    }

    /*
     * Queries tracks using dynamic JPA Specifications and multi-field
     *      sorting rules (defaults to trackId ascending).
     *
     * @param filterParams Object containing filter criteria and
     *      sort directions (time, speed, trackNumber).
     * @return A filtered and sorted list of Track entities.
     */
    public List<Track> filterTracks(FilterParameters filterParams) {
        Specification<Track> spec = TrackSpecifications.filterTracks(filterParams);
        List<Order> orders = new ArrayList<>();
        boolean sorted = false;

        String sortInput = filterParams.getTimeSort();
        String sortDirection = filterParams.getTimeSortDirection();
        if (sortInput != null && !sortInput.isEmpty()) {
            if(sortDirection.equalsIgnoreCase("desc"))
                orders.add(Order.desc("createdTime"));
            else
                orders.add(Order.asc("createdTime"));
            sorted = true;
        }
        sortInput = filterParams.getSpeedSort();
        sortDirection = filterParams.getSpeedSortDirection();
        if (sortInput != null && !sortInput.isEmpty()) {
            if(sortDirection.equalsIgnoreCase("desc"))
                orders.add(Order.desc("speed"));
            else
                orders.add(Order.asc("speed"));
            sorted = true;
        }
        sortInput = filterParams.getTrackNumberSort();
        sortDirection = filterParams.getTrackNumberSortDirection();
        if (sortInput != null && !sortInput.isEmpty()) {
            if(sortDirection.equalsIgnoreCase("desc"))
                orders.add(Order.desc("trackNumber"));
            else
                orders.add(Order.asc("trackNumber"));
            sorted = true;
        }

        // Default fallback sort rule if no sort query params are supplied
        if(!sorted) orders.add(Order.asc("trackId"));
        Sort sort = Sort.by(orders);
        return track_repo.findAll(spec, sort);
    }

    /*
     * Finds groups of ACTIVE tracks from different sources that
     *      likely represent the same real-world object.
     *
     * @return A Map pairing each correlated active Track with
     *      a list containing itself and its matching tracks.
     */
    public Map<Track, List<Track>> correlateTracks() {
        FilterParameters filterParams = new FilterParameters();
        filterParams.setStatus("ACTIVE");
        List<Track> allActiveTracks = filterTracks(filterParams);
        Map<Track, List<Track>> correlationMap = new HashMap<>();
        List<Track> listOfTracks;
        for (Track track : allActiveTracks) {
            listOfTracks = correlateTrack(track, allActiveTracks);
            if (!listOfTracks.isEmpty()) {
                listOfTracks.add(0, track);
                correlationMap.put(track, listOfTracks);
            }
        }
        return correlationMap;
    }

    /*
     * Finds tracks within a search pool that match a target
     *      track's identity type, have a different source,
     *      and fall within <=100 speed difference, <=30 seconds time
     *      difference, and <=2 km distance.
     *
     * @param selected_track The reference Track to correlate against.
     * @param search_pool    Optional list of candidate tracks to restrict the search to.
     * @return A list of correlated Track entities.
     */
    public List<Track> correlateTrack(Track selected_track, List<Track> search_pool) {
        Specification<Track> spec = TrackSpecifications.filterTracksCorrelation(
                selected_track.getSourceId(),
                selected_track.getIdentityType()
        );
        List<Track> listOfTracks = track_repo.findAll(spec);

        List<Track> listOfCorrelatedTracks = new ArrayList<>();
        double distance;
        long time;
        for (Track track : listOfTracks) {
            if (search_pool != null) {
                if (search_pool.isEmpty() || !search_pool.contains(track)) continue;
            }
            if (Math.abs(selected_track.getSpeed() - track.getSpeed()) <= 100) {
                time = Math.abs(Duration.between(selected_track.getCreatedTime(), track.getCreatedTime()).getSeconds());
                if (time <= 30) {
                    distance = calculate_distance(selected_track.getLatitude(), selected_track.getLongitude(),
                            track.getLatitude(), track.getLongitude());
                    if (distance <= 2)
                        listOfCorrelatedTracks.add(track);
                }
            }
        }
        return listOfCorrelatedTracks;
    }

    /*
     * Calculates the great-circle distance in kilometers between
     *      two GPS coordinates using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 in degrees.
     * @param lon1 Longitude of point 1 in degrees.
     * @param lat2 Latitude of point 2 in degrees.
     * @param lon2 Longitude of point 2 in degrees.
     * @return Distance between the two coordinates in kilometers.
     */
    public double calculate_distance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;

        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Differences in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine core math
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Return final distance in kilometers
        return EARTH_RADIUS_KM * c;
    }

    /*
     * Retrieves historical track observations within a time window,
     *      optionally filtered by track number.
     *
     * @param start       Start date-time string (accepts space or 'T' separator).
     * @param end         End date-time string (accepts space or 'T' separator).
     * @param trackNumber Optional track number filter (treated as null if empty).
     * @return A list of matching historical Track entities.
     */
    public List<Track> getTrackHistory(String start, String end, String trackNumber) {
        LocalDateTime startTime = parseFlexibleDateTime(start);
        LocalDateTime endTime = parseFlexibleDateTime(end);
        if (trackNumber.isEmpty()) trackNumber = null;
        return track_repo.selectByTrackNumberAndTimeWindow(startTime, endTime, trackNumber);
    }

    /*
     * Normalizes a date-time string by replacing spaces with 'T'
     *      and parses it into a LocalDateTime.
     *
     * @param input Date-time string (e.g., "2026-01-01 12:00:00" or "2026-01-01T12:00:00").
     * @return Parsed LocalDateTime object.
     */
    private LocalDateTime parseFlexibleDateTime(String input) {
        String normalized = input.trim().replace(" ", "T");
        return LocalDateTime.parse(normalized);
    }

    /*
     * Retrieves only the single latest Track observation per (sourceId, trackNumber) pair.
     *
     * @return A list of the most recent Track entities.
     */
    public List<Track> getLatestTracksView() {
        return track_repo.selectLatestTracksView();
    }

    /*
     * Finds the closest neighboring track (with a different track number) for each latest track
     * and returns deduplicated pairs sorted by distance ascending.
     *
     * @param selectedTrackId Optional track ID string to restrict the
     *      calculation to a single track (or null for all).
     * @return A list of Maps containing metadata for each track,
     *      its nearest neighbor, and distanceKm.
     */
    public List<Map<String, Object>> getNearestUniqueTracks(String selectedTrackId) {
        List<Track> currentTracks = getLatestTracksView();
        // Remove tracks without a track number
        currentTracks = currentTracks.stream()
                .filter(t -> t.getTrackNumber() != null)
                .toList();
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> seenPairs = new HashSet<>();
        for (Track selected : currentTracks) {
            // Filter by selected track ID when provided
            if (selectedTrackId != null &&
                    !String.valueOf(selected.getTrackId()).equals(selectedTrackId)) {
                continue;
            }
            Track nearest = null;
            double minDistance = Double.MAX_VALUE;
            for (Track candidate : currentTracks) {
                // Skip the same track
                if (selected.getTrackId() == candidate.getTrackId()) {
                    continue;
                }
                // Skip observations of the same track number
                if (selected.getTrackNumber().equals(candidate.getTrackNumber())) {
                    continue;
                }
                double distance = calculate_distance(
                        selected.getLatitude(),
                        selected.getLongitude(),
                        candidate.getLatitude(),
                        candidate.getLongitude()
                );
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = candidate;
                }
            }
            if (nearest == null) {
                continue;
            }
            // Prevent repeated pairs such as A-B and B-A
            int firstId = Math.min(selected.getTrackId(), nearest.getTrackId());
            int secondId = Math.max(selected.getTrackId(), nearest.getTrackId());
            String pairKey = firstId + "-" + secondId;
            if (!seenPairs.add(pairKey)) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("trackId", selected.getTrackId());
            item.put("sourceId", selected.getSourceId());
            item.put("trackNumber", selected.getTrackNumber());
            item.put("trackType", selected.getTrackType());
            item.put("nearestTrackId", nearest.getTrackId());
            item.put("nearestSourceId", nearest.getSourceId());
            item.put("nearestTrackNumber", nearest.getTrackNumber());
            item.put("nearestTrackType", nearest.getTrackType());
            item.put("distanceKm", Math.round(minDistance * 1000.0) / 1000.0);
            results.add(item);
        }
        results.sort(Comparator.comparingDouble(r -> ((Number) r.get("distanceKm")).doubleValue()));
        return results;
    }

}
