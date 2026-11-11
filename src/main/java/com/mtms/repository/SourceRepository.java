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
public interface SourceRepository extends CrudRepository<Source, Integer>, JpaSpecificationExecutor {
    Optional<Source> findBySourceId(String id);

    // Update status (ACTIVE, STALE, DISCONNECTED)
    @Modifying
    @Transactional
    @Query("UPDATE Source s SET s.sourceStatus = :newStatus where s.sourceId = :sourceId")
    int updateStatus(@Param("sourceId") String sourceId, @Param("newStatus") String newStatus);

    // Update source based on outcome, Uses native SQL
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
}
