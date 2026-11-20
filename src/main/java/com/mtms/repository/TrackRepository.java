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
public interface TrackRepository extends JpaRepository<Track, Integer>, JpaSpecificationExecutor<Track> {
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

    // Selects tracks within a specific time window and track number
    @Query("SELECT t FROM Track t WHERE (:trackNumber IS NULL OR t.trackNumber = :trackNumber) AND t.createdTime BETWEEN :start AND :end ORDER BY t.createdTime ASC, t.trackId ASC")
    List<Track> selectByTrackNumberAndTimeWindow(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("trackNumber") String trackNumber);
    
    @Query(value = "SELECT " +
            "  t1.track_id AS t1_id, t1.source_id AS t1_source, t1.sequence_number AS t1_seq, t1.track_number AS t1_num, " +
            "  t1.latitude AS t1_lat, t1.longitude AS t1_lon, t1.speed AS t1_speed, t1.identity_type AS t1_id_type, " +
            "  t1.track_status AS t1_status, t1.created_time AS t1_time, " +
            "  t2.track_id AS t2_id, t2.source_id AS t2_source, t2.sequence_number AS t2_seq, t2.track_number AS t2_num, " +
            "  t2.latitude AS t2_lat, t2.longitude AS t2_lon, t2.speed AS t2_speed, t2.identity_type AS t2_id_type, " +
            "  t2.track_status AS t2_status, t2.created_time AS t2_time, " +
            "  ROUND(CAST((6371 * 2 * ASIN(SQRT(" +
            "    POWER(SIN(RADIANS(t2.latitude - t1.latitude) / 2), 2) + " +
            "    COS(RADIANS(t1.latitude)) * COS(RADIANS(t2.latitude)) * " +
            "    POWER(SIN(RADIANS(t2.longitude - t1.longitude) / 2), 2)" +
            "  ))) AS NUMERIC), 3) AS distance_km, " +
            "  ROUND(CAST(ABS(EXTRACT(EPOCH FROM (t1.created_time - t2.created_time))) AS NUMERIC), 1) AS time_diff_sec " +
            "FROM track t1 " +
            "JOIN track t2 ON t1.source_id < t2.source_id " +
            "WHERE ABS(EXTRACT(EPOCH FROM (t1.created_time - t2.created_time))) <= 30 " +
            "AND (6371 * 2 * ASIN(SQRT(" +
            "  POWER(SIN(RADIANS(t2.latitude - t1.latitude) / 2), 2) + " +
            "  COS(RADIANS(t1.latitude)) * COS(RADIANS(t2.latitude)) * " +
            "  POWER(SIN(RADIANS(t2.longitude - t1.longitude) / 2), 2)" +
            "))) <= 2.0 " +
            "ORDER BY t1.created_time DESC, t1.track_id DESC", nativeQuery = true)
    List<Object[]> findCorrelatedTrackPairsRaw();

    // Selects the latest tracks view using window functions in native SQL
    @Query(value = "WITH RankedTracks AS (" +
            "  SELECT *, ROW_NUMBER() OVER(" +
            "    PARTITION BY source_id, track_number " +
            "    ORDER BY created_time DESC, sequence_number DESC" +
            "  ) as row_num FROM track" +
            ") SELECT * FROM RankedTracks WHERE row_num = 1 ORDER BY created_time DESC", nativeQuery = true)
    List<Track> selectLatestTracksView();
}
