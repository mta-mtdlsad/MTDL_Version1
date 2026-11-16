package com.mtms.repository;

import com.mtms.domain.Alert;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends CrudRepository<Alert, Long> {

    List<Alert> findAllByOrderByAlertTimeDescAlertIdDesc();

    @Query("SELECT a FROM Alert a WHERE UPPER(a.alertSeverity) = UPPER(:severity) ORDER BY a.alertTime ASC, a.alertId ASC")
    List<Alert> selectSeverity(@Param("severity") String severity);
}
