package com.mtms.repository;

import com.mtms.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer>, JpaSpecificationExecutor {
    List<Track> findTracksByTrackNumber(String track_number);

    List<Track> findTracksBySourceId(String sourceId);

    List<Track> findTracksByTrackNumberAndSourceId(String track_number, String source_id);

    long deleteAllByTrackNumber(String trackNumber);

    @Query("SELECT t.identityType AS identityType, COUNT(t) AS count FROM Track t GROUP BY t.identityType")
    List<IdentityTypeCount> countByIdentityType();

    // Nested projection interface
    interface IdentityTypeCount {
        String getIdentityType();

        Integer getCount();
    }

    boolean existsBySourceIdAndSequenceNumber(String sourceId, int sequenceNumber);

    @Query("""
              SELECT t FROM Track t
              WHERE t.createdTime BETWEEN :startTime AND :endTime
              ORDER BY t.createdTime ASC, t.trackId ASC
            """)
    List<Track> getAllTracksTimeframe(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
