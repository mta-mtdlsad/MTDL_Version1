package com.mtms.repository;

import com.mtms.domain.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

//@Repository
public interface TrackRepository extends CrudRepository<Track, Integer> {

    List<Track> findByTrackNumber(String trackNumber);

    Integer deleteByTrackNumber(String trackNumber);

    List<Track> findByTrackNumberAndSourceId(String trackNumber, String sourceId);

    List<Track> findByIdentityOrTrackStatusOrSpeedGreaterThanEqual(String identity, String status, Double minSpeed);

    List<Track> findByIdentity(String identity);

    List<Track> findBySpeedGreaterThanEqual(double speed);

    List<Track> findByTrackStatus(String status);

    @Query(value = "SELECT identity, COUNT(*) AS count FROM Track GROUP BY identity", nativeQuery = true)
    List<Map<String, Integer>> findIdentityCount();
}
