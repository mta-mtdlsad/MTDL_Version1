package com.mtms.service;

import com.mtms.util.FilterParameters;
import com.mtms.model.Track;
import com.mtms.repository.SourceRepository;
import com.mtms.repository.TrackRepository;
import com.mtms.util.TrackSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrackService {

    @Autowired
    private TrackRepository track_repo;

    // Saves track using TrackRepository and returns boolean for result
    public boolean saveTrack(Track track) {
        boolean exists = track_repo.existsBySourceIdAndSequenceNumber(track.getSourceId(), track.getSequenceNumber());
        if (exists) {
            return false;
        } else {
            track_repo.save(track);
            return true;
        }
    }

    // Returns List of all tracks using TrackRepository
    public List<Track> getAllTracks() {
        List<Track> listOfTracks = new ArrayList<>();
        track_repo.findAll().forEach(listOfTracks::add);
        return listOfTracks;

    }

    // Returns Track based on given ID using TrackRepository
    /*public Track findTrackById(int id) {
        return track_repo.findById(id).get();
    }*/

    // Returns Tracks based on given trackNumber using TrackRepository
    public List<Track> findTracksByNumber(String number) {
        return track_repo.findTracksByTrackNumber(number);
    }

    @Transactional
    // Deletes Tracks based on given trackNumber using TrackRepository
    public Boolean deleteTracksByTrackNumber(String number) {
        long deleted = track_repo.deleteAllByTrackNumber(number);
        return deleted > 0;
    }

    // Returns List of track observations for a specific trackNumber and sourceId using TrackRepository
    public List<Track> getAllTrackHistory(String trackNo, String sourceId) {
        return track_repo.findTracksByTrackNumberAndSourceId(trackNo, sourceId);
    }

    // Returns the highest sequenceNumber given a specific sourceId using TrackRepository
    public int getHighestSequence(String sourceId) {
        List<Track> listOfTracks = track_repo.findTracksBySourceId(sourceId);
        return listOfTracks.stream().mapToInt(Track::getSequenceNumber).max().orElse(0);
    }

    // Returns identityType counts using TrackRepository
    public Map<String, Integer> getIdentityStatistics() {
        String[] identityTypes = {"FRIEND", "HOSTILE", "NEUTRAL", "UNKNOWN"};
        Map<String, Integer> statistics = new LinkedHashMap<>();
        List<TrackRepository.IdentityTypeCount> results = track_repo.countByIdentityType();
        for (TrackRepository.IdentityTypeCount result : results) {
            statistics.put(result.getIdentityType(), result.getCount());
        }
        for(String type : identityTypes) {
            if(!statistics.containsKey(type)) {
                statistics.put(type, 0);
            }
        }
        return statistics;
    }

    // Returns List of tracks based on specified filter parameters using TrackRepository
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

    // Returns any correlated tracks
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

    // Returns any correlated tracks in search_pool for a specific track
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

    // Distance calculation (from lat,lon to km) for correlation
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


    public List<Track> getTrackHistory(String start, String end, String trackNumber) {
        LocalDateTime startTime = parseFlexibleDateTime(start);
        LocalDateTime endTime = parseFlexibleDateTime(end);
        if (trackNumber.isEmpty()) trackNumber = null;
        return track_repo.selectByTrackNumberAndTimeWindow(startTime, endTime, trackNumber);
    }
    private LocalDateTime parseFlexibleDateTime(String input) {
        String normalized = input.trim().replace(" ", "T");
        return LocalDateTime.parse(normalized);
    }

    public List<Track> getLatestTracksView() {
        return track_repo.selectLatestTracksView();
    }

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
