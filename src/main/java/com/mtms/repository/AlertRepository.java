package com.mtms.repository;

import com.mtms.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    // Retrieve alerts ordered by their creation time
    List<Alert> findAllByOrderByAlertTimeAsc();

    @Query("SELECT a.alertSeverity AS severityType, COUNT(a) AS count FROM Alert a GROUP BY a.alertSeverity")
    List<SeverityTypeCount> countBySeverity();

    // Nested projection interface
    interface SeverityTypeCount {
        String getSeverityType();
        Integer getCount();
    }
}