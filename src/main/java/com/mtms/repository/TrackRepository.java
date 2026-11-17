package com.mtms.repository;

import com.mtms.domain.Track;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access layer for managing track entities and spatial calculations using Spring Data JPA.
 * Registered as a Spring Repository component.
 */
@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {

    @Query("SELECT COUNT(t) > 0 FROM Track t WHERE t.sourceId = :sourceId AND t.sequenceNumber = :sequenceNumber")
    boolean existsBySourceIdAndSequenceNumber(@Param("sourceId") String sourceId, @Param("sequenceNumber") Integer sequenceNumber);

    /**
     * Resolves the latest recorded synthetic creation timestamp for a specific source ID.
     */
    @Query("SELECT MAX(t.createdTime) FROM Track t WHERE t.sourceId = :sourceId")
    LocalDateTime selectLatestTimestampForSource(@Param("sourceId") String sourceId);

    // Resolves the highest sequence number recorded for a specific source ID
    @Query("SELECT MAX(t.sequenceNumber) FROM Track t WHERE t.sourceId = :sourceId")
    Integer selectLatestSequenceForSource(@Param("sourceId") String sourceId);

    // Removes database records matching specified track label using modifying delete query
    @Modifying
    @Transactional
    int deleteByTrackNumber(String trackNumber);

    // Searches tracks by track number ordered descending using derived query syntax
    List<Track> findByTrackNumberOrderByCreatedTimeDescTrackIdDesc(String trackNumber);

    // Finds tracks by identity type matching case-insensitive input
    @Query("SELECT t FROM Track t WHERE UPPER(t.identityType) = UPPER(:identity) ORDER BY t.createdTime ASC")
    List<Track> findByIdentity(@Param("identity") String identity);

    // Finds high speed tracks exceeding threshold speed order descending
    @Query("SELECT t FROM Track t WHERE t.speed > :thresholdSpeed ORDER BY t.speed DESC")
    List<Track> findHighSpeedTracks(@Param("thresholdSpeed") double thresholdSpeed);

    // Fetches all tracks stored in the database ordered for replay data
    List<Track> findAllByOrderByCreatedTimeAscTrackIdAsc();

    // Selects tracks within a specific time window
    @Query("SELECT t FROM Track t WHERE t.createdTime BETWEEN :start AND :end ORDER BY t.createdTime ASC, t.trackId ASC")
    List<Track> selectByTimeWindow(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Selects tracks filtered by status
    @Query("SELECT t FROM Track t WHERE UPPER(t.trackStatus) = UPPER(:status) ORDER BY t.createdTime ASC, t.trackId ASC")
    List<Track> selectByStatus(@Param("status") String status);

    // Finds tracks by combined dynamic criteria (identity, status, min speed)
//    @Query("SELECT t FROM Track t WHERE (:identity IS NULL OR UPPER(t.identityType) = UPPER(:identity)) " +
//            "AND (:status IS NULL OR UPPER(t.trackStatus) = UPPER(:status)) " +
//            "AND (:minSpeed IS NULL OR t.speed >= :minSpeed) " +
//            "ORDER BY t.createdTime DESC, t.trackId DESC")
    @Query("SELECT t FROM Track t WHERE UPPER(t.identityType) = UPPER(COALESCE(:identity, t.identityType)) " +
            "AND UPPER(t.trackStatus) = UPPER(COALESCE(:status, t.trackStatus)) " +
            "AND t.speed >= COALESCE(:minSpeed, t.speed) " +
            "ORDER BY t.createdTime DESC, t.trackId DESC")
    List<Track> findTracksByCombinedCriteria(
            @Param("identity") String identity,
            @Param("status") String status,
            @Param("minSpeed") Double minSpeed
    );

    // Finds correlated tracks within temporal and spatial proximity bounds using native PostgreSQL query
    @Query(value = "SELECT DISTINCT t1.* FROM tracks t1 " +
            "JOIN tracks t2 ON t1.source_id != t2.source_id " +
            "WHERE ABS(EXTRACT(EPOCH FROM (t1.created_time - t2.created_time))) <= 30 " +
            "AND (6371 * 2 * ASIN(SQRT(" +
            "  POWER(SIN(RADIANS(t2.latitude - t1.latitude) / 2), 2) + " +
            "  COS(RADIANS(t1.latitude)) * COS(RADIANS(t2.latitude)) * " +
            "  POWER(SIN(RADIANS(t2.longitude - t1.longitude) / 2), 2)" +
            "))) <= 2.0 " +
            "ORDER BY t1.created_time DESC, t1.track_id DESC", nativeQuery = true)
    List<Track> findCorrelatedTracks();

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
            "FROM tracks t1 " +
            "JOIN tracks t2 ON t1.source_id < t2.source_id " +
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
            "  ) as row_num FROM tracks" +
            ") SELECT * FROM RankedTracks WHERE row_num = 1 ORDER BY created_time DESC", nativeQuery = true)
    List<Track> selectLatestTracksView();

    @Query(value = "WITH LatestPerSource AS (" +
            "  SELECT *, ROW_NUMBER() OVER(" +
            "    PARTITION BY source_id " +
            "    ORDER BY created_time DESC, sequence_number DESC" +
            "  ) AS rn FROM tracks WHERE source_id IS NOT NULL" +
            "), CurrentSources AS (" +
            "  SELECT * FROM LatestPerSource WHERE rn = 1" +
            ") " +
            "SELECT t1.source_id AS source_id, " +
            "       n.nearest_source_id AS nearest_source_id, " +
            "       ROUND(CAST(n.dist AS NUMERIC), 3) AS distance_km " +
            "FROM CurrentSources t1 " +
            "CROSS JOIN LATERAL (" +
            "  SELECT t2.source_id AS nearest_source_id, " +
            "         (6371 * 2 * ASIN(SQRT(" +
            "           POWER(SIN(RADIANS(t2.latitude - t1.latitude) / 2), 2) + " +
            "           COS(RADIANS(t1.latitude)) * COS(RADIANS(t2.latitude)) * " +
            "           POWER(SIN(RADIANS(t2.longitude - t1.longitude) / 2), 2)" +
            "         ))) AS dist " +
            "  FROM CurrentSources t2 " +
            "  WHERE t1.source_id != t2.source_id " +
            "  ORDER BY dist ASC LIMIT 1" +
            ") n " +
            "ORDER BY distance_km ASC, t1.source_id ASC", nativeQuery = true)
    List<Object[]> selectNearestTracksWithDistanceRaw();




    // Retrieves all latest sequence numbers grouped by source ID
    @Query("SELECT t.sourceId, MAX(t.sequenceNumber) FROM Track t GROUP BY t.sourceId")
    List<Object[]> getLatestSequenceMapRaw();
}
