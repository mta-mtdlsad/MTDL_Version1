package com.mtms.repository;

import com.mtms.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    /*
     * Retrieves all alerts from the database ordered from
     *      oldest to newest by alert time.
     *
     * @return A chronologically sorted list of all Alert entities.
     */
    List<Alert> findAllByOrderByAlertTimeAsc();

    /*
     * Groups all alerts by their severity level and counts
     *      how many alerts exist for each severity.
     *
     * @return A list of SeverityTypeCount projections containing
     *      each severity type and its total count.
     */
    @Query("SELECT a.alertSeverity AS severityType, COUNT(a) AS count FROM Alert a GROUP BY a.alertSeverity")
    List<SeverityTypeCount> countBySeverity();

    // Nested projection interface
    interface SeverityTypeCount {
        String getSeverityType();
        Integer getCount();
    }
}