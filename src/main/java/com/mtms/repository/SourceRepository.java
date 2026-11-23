package com.mtms.repository;

import com.mtms.model.Source;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface SourceRepository extends CrudRepository<Source, Integer>, JpaSpecificationExecutor<Source> {
    /*
     * Finds a Source record by its unique source identifier string.
     *
     * @param id The unique source ID.
     * @return An Optional containing the Source if found, or empty if not found.
     */
    Optional<Source> findBySourceId(String id);

    /*
     * Updates the operational status (ACTIVE, STALE, DISCONNECTED)
     *      of a specific source.
     *
     * @param sourceId  The unique source ID to update.
     * @param newStatus The new status string to assign.
     * @return The number of rows updated (1 if found, 0 if not).
     */
    @Modifying
    @Transactional
    @Query("UPDATE Source s SET s.sourceStatus = :newStatus where s.sourceId = :sourceId")
    int updateStatus(@Param("sourceId") String sourceId, @Param("newStatus") String newStatus);

    /*
     * Inserts a new source or updates an existing source's timestamps,
     *      highest sequence, and processing counters (upsert).
     *
     * @param sourceId   The unique source ID.
     * @param receivedAt The timestamp when the message was received.
     * @param highest    The sequence number to compare against the current highest sequence.
     * @param accepted   Count increment for accepted messages (typically 1 or 0).
     * @param rejected   Count increment for rejected messages (typically 1 or 0).
     * @param duplicate  Count increment for duplicate messages (typically 1 or 0).
     * @param failure    Count increment for processing failures (typically 1 or 0).
     * @return The number of affected database rows (1 on successful insert/update).
     */
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO Source (
              source_id,
              source_status,
              first_seen,
              last_seen,
              highest_sequence,
              received_count,
              accepted_count,
              rejected_count,
              duplicate_count,
              processing_failure_count
            )
            VALUES (
              :sourceId,
              'ACTIVE',
              :receivedAt,
              :receivedAt,
              :highest,
              1,
              :accepted,
              :rejected,
              :duplicate,
              :failure
            )
            ON CONFLICT (source_id) DO UPDATE SET
              source_status = 'ACTIVE',
              first_seen = COALESCE(
                Source.first_seen,
                EXCLUDED.first_seen
              ),
              last_seen = GREATEST(
                Source.last_seen,
                EXCLUDED.last_seen
              ),
              highest_sequence = GREATEST(
                COALESCE(Source.highest_sequence, 0),
                EXCLUDED.highest_sequence
              ),
              received_count =
                COALESCE(Source.received_count, 0) + 1,
              accepted_count =
                COALESCE(Source.accepted_count, 0)
                + EXCLUDED.accepted_count,
              rejected_count =
                COALESCE(Source.rejected_count, 0)
                + EXCLUDED.rejected_count,
              duplicate_count =
                COALESCE(Source.duplicate_count, 0)
                + EXCLUDED.duplicate_count,
              processing_failure_count =
                COALESCE(Source.processing_failure_count, 0)
                + EXCLUDED.processing_failure_count
            """, nativeQuery = true)
    int recordOutcome(
            @Param("sourceId") String sourceId,
            @Param("receivedAt") LocalDateTime receivedAt,
            @Param("highest") int highest,
            @Param("accepted") int accepted,
            @Param("rejected") int rejected,
            @Param("duplicate") int duplicate,
            @Param("failure") int failure
    );

    /*
     * Sets a source's status to 'ACTIVE' only if its
     *      current status is not already 'ACTIVE'.
     *
     * @param sourceId The unique source ID to activate.
     * @return The number of rows updated (1 if status changed to ACTIVE,
     *      0 if already ACTIVE or not found).
     */
    @Modifying
    @Transactional
    @Query("""
            
            UPDATE Source s
            
            SET s.sourceStatus = 'ACTIVE'
            
            WHERE s.sourceId = :sourceId
            
            AND s.sourceStatus <> 'ACTIVE'
            
            """)
    int markSourceActive(@Param("sourceId") String sourceId);
}