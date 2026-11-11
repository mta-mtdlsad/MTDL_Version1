package com.mtms.repository;

import com.mtms.model.Track;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TrackRepository extends CrudRepository<Track, Integer>,  JpaSpecificationExecutor{
    List<Track> findTracksByTrackNumber(String track_number);
    List<Track> findTracksBySourceId(String sourceId);
    List<Track> findTracksByTrackNumberAndSourceId(String track_number, String source_id);
    void deleteAllByTrackNumber(String trackNumber);

}
