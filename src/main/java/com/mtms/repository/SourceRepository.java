package com.mtms.repository;

import com.mtms.domain.Source;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SourceRepository extends CrudRepository<Source, String> {

    Source findBySourceId(String sourceId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO sources (source_id, first_seen_time, last_received_time, latest_sequence, source_status, received_count, accepted_count, rejected_count, duplicate_count, processing_failure_count) " +
            "VALUES (:sourceId, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :seqVal, 'ACTIVE', 1, :isAcc, :isRej, :isDup, :isFail) " +
            "ON CONFLICT (source_id) DO UPDATE SET " +
            "last_received_time = CURRENT_TIMESTAMP, " +
            "source_status = 'ACTIVE', " +
            "latest_sequence = GREATEST(sources.latest_sequence, :seqVal), " +
            "received_count = sources.received_count + 1, " +
            "accepted_count = sources.accepted_count + :isAcc, " +
            "rejected_count = sources.rejected_count + :isRej, " +
            "duplicate_count = sources.duplicate_count + :isDup, " +
            "processing_failure_count = sources.processing_failure_count + :isFail", nativeQuery = true)
    void syncSourceMetrics(
            @Param("sourceId") String sourceId,
            @Param("seqVal") int seqVal,
            @Param("isAcc") int isAcc,
            @Param("isRej") int isRej,
            @Param("isDup") int isDup,
            @Param("isFail") int isFail
    );

    @Query("SELECT s FROM Source s WHERE s.sourceStatus IN ('ACTIVE', 'STALE')")
    List<Source> findByStatusActiveOrStale();

    @Modifying
    @Transactional
    @Query("UPDATE Source s SET s.sourceStatus = :status WHERE s.sourceId = :sourceId")
    void updateSourceStatus(@Param("sourceId") String sourceId, @Param("status") String status);
}
