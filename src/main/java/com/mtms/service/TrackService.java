package com.mtms.service;

import com.mtms.model.FilterParameters;
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

    @Autowired
    private SourceHealthService sourceHealth;

    @Autowired
    private SourceRepository source_repo;

    public Track findTrackById(int id) {
        return track_repo.findById(id).get();
    }

    public List<Track> findTracksByNumber(String number) {
        return track_repo.findTracksByTrackNumber(number);
    }

    public List<Track> findTracksBySource(String source_id) {
        return track_repo.findTracksBySourceId(source_id);
    }

//    @Transactional
//    public Track createTrackFromApi(Track track) {
//        // 1. Save the track
//        Track saved = track_repo.save(track);
//
//        // 2. Register or update its source statistics
//        sourceHealth.recordOutcome(
//                saved.getSourceId(),
//                LocalDateTime.now(),
//                "ACCEPTED",
//                saved.getSequenceNumber()
//        );
//        // 3. Update the source status
//        source_repo.updateStatus(saved.getSourceId(), "ACTIVE");
//        return saved;
//    }


    public Boolean deleteTrack(int id) {
        track_repo.deleteById(id);
        if (findTrackById(id) == null) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Boolean deleteTracksByTrackNumber(String number) {
        long deleted = track_repo.deleteAllByTrackNumber(number);
        return deleted > 0;
    }

    public boolean saveTrack(Track track) {
        boolean exists = track_repo.existsBySourceIdAndSequenceNumber(track.getSourceId(), track.getSequenceNumber());

        if (exists) {
            return false;
        } else {
            track_repo.save(track);
            return true;
        }

    }

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

    public int getHighestSequence(String sourceId) throws SQLException {
        List<Track> listOfTracks = track_repo.findTracksBySourceId(sourceId);
        return listOfTracks.stream().mapToInt(Track::getSequenceNumber).max().orElse(0);
    }

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

    public Map<Track, List<Track>> correlateTracks() {
        FilterParameters filterParams = new FilterParameters();
        filterParams.setStatus("ACTIVE");
        List<Track> allActiveTracks = filterTracks(filterParams);
        Map<Track, List<Track>> correlationMap = new HashMap<>();
        List<Track> updatedActiveTracks = new ArrayList<>(allActiveTracks);
        List<Track> listOfTracks;
        for (Track track : allActiveTracks) {
            if (!updatedActiveTracks.contains(track)) continue;
            listOfTracks = correlateTrack(track, updatedActiveTracks);
            if (!listOfTracks.isEmpty()) {
                correlationMap.put(track, listOfTracks);
                for (Track correlatedTrack : listOfTracks)
                    updatedActiveTracks.remove(correlatedTrack);
            }
            updatedActiveTracks.remove(track);
            if (updatedActiveTracks.isEmpty()) break;
        }
        return correlationMap;
    }

    public List<Track> getAllTracks() {
        List<Track> listOfTracks = new ArrayList<>();
        track_repo.findAll().forEach(listOfTracks::add);
        return listOfTracks;

    }


    public List<Track> getAllTrackHistory(String trackNo, String sourceId) {
        return track_repo.findTracksByTrackNumberAndSourceId(trackNo, sourceId);
    }

    public List getIdentityStatistics() {return track_repo.countByIdentityType();}

}
