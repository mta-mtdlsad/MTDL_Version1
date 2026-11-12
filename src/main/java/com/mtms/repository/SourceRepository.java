package com.mtms.repository;


import com.mtms.domain.Source;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SourceRepository extends CrudRepository<Source, String> {

//    List<Source> findAllSources(String sourceId);

    Source findBySourceId(String sourceId);

//    boolean updateLatestSequenceIfValid(String sourceId, long sequenceNumber);


    //    void checkSourceHealth(String trackNumber);
//
//    String calculateSourceStatus(Source source, LocalDateTime checkTime);
//
//    boolean receiveMessage(String sourceId, long sequenceNumber);
//
//

//    void updateLastReceivedTime(String sourceId, LocalDateTime receivedTime);
//
//    boolean updateSourceStatus(String sourceId, String oldStatus, String newStatus, LocalDateTime checkTime, Alert alert);
}