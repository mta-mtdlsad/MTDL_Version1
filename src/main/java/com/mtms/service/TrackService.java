package com.mtms.service;

import com.mtms.constant.MTMSConstants;
import com.mtms.domain.Track;
import com.mtms.repository.TrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class TrackService {
    private static final Logger log = LoggerFactory.getLogger(TrackService.class);

    private final TrackRepository trackRepository;
    private final PathService pathService;
    private final ValidationService validationService;
    private final AlertService alertService;
    private final SourceService sourceService;

    public TrackService(TrackRepository trackRepository, PathService pathService, ValidationService validationService, AlertService alertService, SourceService sourceService) {
        this.trackRepository = trackRepository;
        this.pathService = pathService;
        this.validationService = validationService;
        this.alertService = alertService;
        this.sourceService = sourceService;
    }

    public List<Track> getAllTracks() {
        List<Track> records = new ArrayList<>();
        trackRepository.findAll().forEach(records::add);
        return records;
    }


    public List<Track> searchTracks(String trackNumber) {
        return trackRepository.findByTrackNumberOrderByCreatedTimeDescTrackIdDesc(trackNumber);
    }

    public List<Track> filterTracks(String identity, String status, Double minSpeed) {
        return trackRepository.findTracksByCombinedCriteria(identity, status, minSpeed);
    }

    public List<Track> getTrackHistory(String start, String end) {
        LocalDateTime startTime = parseFlexibleDateTime(start);
        LocalDateTime endTime = parseFlexibleDateTime(end);
        return trackRepository.selectByTimeWindow(startTime, endTime);
    }

    private LocalDateTime parseFlexibleDateTime(String input) {
        String normalized = input.trim().replace(" ", "T");
        return LocalDateTime.parse(normalized);
    }

    public int deleteTracksByNumber(String trackNumber) {
        return trackRepository.deleteByTrackNumber(trackNumber);
    }

    public List<Track> getCorrelatedTracks() {
        return trackRepository.findCorrelatedTracks();
    }
    public List<Map<String, Object>> getCorrelatedTrackGroups() {
        List<Object[]> pairs = trackRepository.findCorrelatedTrackPairsRaw();
        if (pairs.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Map<String, Object>> trackDetails = new LinkedHashMap<>();
        Map<Long, Set<Long>> adjacency = new LinkedHashMap<>();
        Map<Long, Double> minDistanceByTrack = new HashMap<>();
        Map<Long, Double> minTimeDiffByTrack = new HashMap<>();

        for (Object[] row : pairs) {
            Long id1 = ((Number) row[0]).longValue();
            Long id2 = ((Number) row[10]).longValue();
            double distKm = ((Number) row[20]).doubleValue();
            double timeDiffSec = ((Number) row[21]).doubleValue();

            trackDetails.putIfAbsent(id1, extractTrackMap(row, 0));
            trackDetails.putIfAbsent(id2, extractTrackMap(row, 10));

            adjacency.computeIfAbsent(id1, k -> new LinkedHashSet<>()).add(id2);
            adjacency.computeIfAbsent(id2, k -> new LinkedHashSet<>()).add(id1);

            minDistanceByTrack.merge(id1, distKm, Math::min);
            minDistanceByTrack.merge(id2, distKm, Math::min);
            minTimeDiffByTrack.merge(id1, timeDiffSec, Math::min);
            minTimeDiffByTrack.merge(id2, timeDiffSec, Math::min);
        }

        List<Map<String, Object>> groups = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        int groupNumber = 1;

        for (Long startId : trackDetails.keySet()) {
            if (visited.contains(startId)) continue;

            List<Map<String, Object>> groupTracks = new ArrayList<>();
            Queue<Long> queue = new LinkedList<>();
            queue.add(startId);
            visited.add(startId);

            double maxGroupDist = 0.0;
            double maxGroupTimeDiff = 0.0;

            while (!queue.isEmpty()) {
                Long currentId = queue.poll();
                Map<String, Object> trackMap = trackDetails.get(currentId);
                groupTracks.add(trackMap);

                maxGroupDist = Math.max(maxGroupDist, minDistanceByTrack.getOrDefault(currentId, 0.0));
                maxGroupTimeDiff = Math.max(maxGroupTimeDiff, minTimeDiffByTrack.getOrDefault(currentId, 0.0));

                for (Long neighbor : adjacency.getOrDefault(currentId, Collections.emptySet())) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }

            groupTracks.sort(Comparator.comparing(t -> String.valueOf(t.get("sourceId"))));

            Map<String, Object> groupObj = new LinkedHashMap<>();
            groupObj.put("groupId", groupNumber++);
            groupObj.put("sourceCount", groupTracks.size());
            groupObj.put("maxDistanceKm", Math.round(maxGroupDist * 1000.0) / 1000.0);
            groupObj.put("maxTimeDiffSec", Math.round(maxGroupTimeDiff * 10.0) / 10.0);
            groupObj.put("tracks", groupTracks);
            groups.add(groupObj);
        }

        return groups;
    }

    private Map<String, Object> extractTrackMap(Object[] row, int offset) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("trackId", row[offset]);
        map.put("sourceId", row[offset + 1]);
        map.put("sequenceNumber", row[offset + 2]);
        map.put("trackNumber", row[offset + 3]);
        map.put("latitude", row[offset + 4]);
        map.put("longitude", row[offset + 5]);
        map.put("speed", row[offset + 6]);
        map.put("identityType", row[offset + 7]);
        map.put("trackStatus", row[offset + 8]);
        map.put("createdTime", row[offset + 9] != null ? row[offset + 9].toString() : null);
        return map;
    }


    public List<Track> getLatestTracksView() {
        return trackRepository.selectLatestTracksView();
    }

    public List<Map<String, Object>> getNearestTracksWithDistance() {
        List<Object[]> rows = trackRepository.selectNearestTracksWithDistanceRaw();
        List<Map<String, Object>> results = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("sourceId", row[0]);
            item.put("nearestSourceId", row[1]);
            item.put("distanceKm", row[2]);
            results.add(item);
        }
        return results;
    }



    public Map<String, Integer> getLatestSequenceMap() {
        Map<String, Integer> latestSequences = new HashMap<>();
        try {
            List<Object[]> rawSequences = trackRepository.getLatestSequenceMapRaw();
            for (Object[] row : rawSequences) {
                if (row != null && row.length >= 2 && row[0] != null) {
                    String sourceId = (String) row[0];
                    Integer maxSeq = row[1] != null ? ((Number) row[1]).intValue() : 0;
                    latestSequences.put(sourceId, maxSeq);
                }
            }
        } catch (Exception e) {
            log.error("Error fetching latest sequence map: {}", e.getMessage());
        }
        return latestSequences;
    }

    public void processTrack(Track track) {
        if (track == null) return;

        track.setReceiveTime(LocalDateTime.now());

        // 1. Duplicate check -> received + 1, duplicate + 1, rejected + 1, failure + 0
        if (track.getSourceId() != null && track.getSequenceNumber() != null &&
                trackRepository.existsBySourceIdAndSequenceNumber(track.getSourceId(), track.getSequenceNumber())) {
            handleDuplicate(track);
            return;
        }

        // 2. Out-of-order check -> received + 1, rejected + 1, failure + 0
        LocalDateTime latestSavedTime = trackRepository.selectLatestTimestampForSource(track.getSourceId());
        Integer latestSequence = trackRepository.selectLatestSequenceForSource(track.getSourceId());

        boolean isOlderTimestamp = (latestSavedTime != null && track.getCreatedTime() != null && track.getCreatedTime().isBefore(latestSavedTime));
        boolean isSmallerSequence = (latestSequence != null && track.getSequenceNumber() != null && track.getSequenceNumber() < latestSequence);

        if (isOlderTimestamp || isSmallerSequence) {
            String reason = isOlderTimestamp
                    ? "Incoming timestamp is older than current source history state"
                    : "Sequence number is smaller than the last sequence number of that source";
            alertService.createAlert(track.getTrackNumber(), track.getSourceId(), "Out-of-order message: " + reason, "Out-of-order message");
            sourceService.syncSourceMetrics(track.getSourceId(), track.getSequenceNumber(), "REJECTED", 0);
            return;
        }

        // 3. ValidationService check -> received + 1, rejected + 1, failure + errors.size()
        List<String> errors = validationService.getValidationErrors(track);
        if (errors.isEmpty()) {
            saveTrackWithAssociations(track);
        } else {
            sourceService.syncSourceMetrics(track.getSourceId(), track.getSequenceNumber(), "REJECTED", errors.size());
            for (String error : errors) {
                alertService.createAlert(track.getTrackNumber(), track.getSourceId(), error, "Track validation failure");
            }
        }
    }


    private void saveTrackWithAssociations(Track track) {
        try {
            // Upsert source FIRST so fk_tracks_source is guaranteed to be satisfied
            if (track.getSourceId() != null && !track.getSourceId().trim().isEmpty()) {
                sourceService.syncSourceMetrics(track.getSourceId(), track.getSequenceNumber(), "ACCEPTED");
            }

            if (track.getPathId() == null) {
                Long matchedPathId = pathService.findMatchingPathIdForCoordinates(track.getLatitude(), track.getLongitude(), 1.5);
                if (matchedPathId != null) {
                    track.setPathId(matchedPathId);
                }
            }
            System.out.println("Track Saved: "+track);
            trackRepository.save(track);
        } catch (DataIntegrityViolationException e) {
            // Handles concurrent duplicate packets arriving on parallel UDP threads
            handleDuplicate(track);
        }
    }

    private void handleDuplicate(Track track) {
        alertService.createAlert(track.getTrackNumber(), track.getSourceId(), "Sequence Number Repeated Alert", "Duplicate message");
        sourceService.syncSourceMetrics(track.getSourceId(), track.getSequenceNumber(), "DUPLICATE");
    }
}
