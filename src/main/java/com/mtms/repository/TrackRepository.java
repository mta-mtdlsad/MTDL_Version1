package com.mtms.repository;

import com.mtms.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer>,  JpaSpecificationExecutor {
    List<Track> findTracksByTrackNumber(String track_number);
    List<Track> findTracksBySourceId(String sourceId);
    List<Track> findTracksByTrackNumberAndSourceId(String track_number, String source_id);
    long deleteAllByTrackNumber(String trackNumber);
    @Query("SELECT t.identityType AS identityType, COUNT(t) AS count FROM Track t GROUP BY t.identityType")
    List countByIdentityType();

    // Nested projection interface
    interface IdentityTypeCount {
        String getIdentityType();
        Long getCount();
    }

    boolean existsBySourceIdAndSequenceNumber(String sourceId, int sequenceNumber);
}
