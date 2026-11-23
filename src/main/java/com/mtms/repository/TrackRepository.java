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
    /*
     * Retrieves all tracks matching a specific track number.
     *
     * @param track_number The unique identifier string of the track.
     * @return A list of matching Track entities (empty if none found).
     */
    List<Track> findTracksByTrackNumber(String track_number);

    /*
     * Retrieves all tracks reported by a specific data source.
     *
     * @param sourceId The identifier of the source system/sensor.
     * @return A list of Track entities originating from the given source.
     */
    List<Track> findTracksBySourceId(String sourceId);

    /*
     * Retrieves all tracks matching both a specific track number and source ID.
     *
     * @param track_number The unique identifier string of the track.
     * @param source_id    The identifier of the source system/sensor.
     * @return A list of Track entities matching both criteria.
     */
    List<Track> findTracksByTrackNumberAndSourceId(String track_number, String source_id);

    /*
     * Deletes all track records associated with a given track number.
     *
     * @param trackNumber The track number of the records to delete.
     * @return The total number of deleted records.
     */
    long deleteAllByTrackNumber(String trackNumber);

    /*
     * Groups all tracks by their identity type and counts how many belong to each category.
     *
     * @return A list of IdentityTypeCount projections containing each identity
     *      type and its total count.
     */
    @Query("SELECT t.identityType AS identityType, COUNT(t) AS count FROM Track t GROUP BY t.identityType")
    List<IdentityTypeCount> countByIdentityType();

    // Nested projection interface
    interface IdentityTypeCount {
        String getIdentityType();

        Integer getCount();
    }

    /*
     * Checks whether a track record already exists for a given source ID
     *      and sequence number (used for duplicate detection).
     *
     * @param sourceId       The identifier of the source system/sensor.
     * @param sequenceNumber The message sequence number from that source.
     * @return true if a matching record exists, false otherwise.
     */
    boolean existsBySourceIdAndSequenceNumber(String sourceId, int sequenceNumber);

    /*
     * Retrieves all tracks created within a specified time range,
     *      sorted chronologically by creation time and track ID.
     *
     * @param startTime The start of the time range (inclusive).
     * @param endTime   The end of the time range (inclusive).
     * @return A sorted list of Track entities created within the timeframe.
     */
    @Query("""
              SELECT t FROM Track t
              WHERE t.createdTime BETWEEN :startTime AND :endTime
              ORDER BY t.createdTime ASC, t.trackId ASC
            """)
    List<Track> getAllTracksTimeframe(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /*
     * Retrieves tracks within a specific time window,
     *      optionally filtered by a specific track number.
     *
     * @param start       The start of the time window (inclusive).
     * @param end         The end of the time window (inclusive).
     * @param trackNumber The track number to filter by, or null
     *      to return all tracks in the time window.
     * @return A sorted list of matching Track entities.
     */
    @Query("SELECT t FROM Track t WHERE (:trackNumber IS NULL OR t.trackNumber = :trackNumber) AND t.createdTime BETWEEN :start AND :end ORDER BY t.createdTime ASC, t.trackId ASC")
    List<Track> selectByTrackNumberAndTimeWindow(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("trackNumber") String trackNumber);

    /*
     * Finds pairs of tracks from different sources reported within 30 seconds
     *      and 2.0 km of each other using the Haversine formula.
     *
     * @return A list of Object[] rows, each containing fields for track 1,
     *      fields for track 2, calculated distance_km, and time_diff_sec.
     */
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

    /*
     * Retrieves only the single most recent Track record for
     *      each unique (source_id, track_number) combination.
     *
     * @return A list of the latest Track entities ordered by creation time descending.
     */
    @Query(value = "WITH RankedTracks AS (" +
            "  SELECT *, ROW_NUMBER() OVER(" +
            "    PARTITION BY source_id, track_number " +
            "    ORDER BY created_time DESC, sequence_number DESC" +
            "  ) as row_num FROM track" +
            ") SELECT * FROM RankedTracks WHERE row_num = 1 ORDER BY created_time DESC", nativeQuery = true)
    List<Track> selectLatestTracksView();
}
