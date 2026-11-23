package com.mtms.service;

import com.mtms.model.Alert;
import com.mtms.model.Track;
import com.mtms.util.ValidationResult;
import com.mtms.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {
    @Autowired
    private AlertRepository alert_repo;

    /*
     * Persists an Alert entity to the database.
     *
     * @param alert The Alert object to save.
     * @return The saved Alert entity (including generated ID).
     */
    public Alert saveAlert(Alert alert) {
        return alert_repo.save(alert);
    }

    /**=
     * Retrieves all alerts from the database ordered
     *      chronologically from oldest to newest.
     *
     * @return A list of all Alert entities sorted by alert time ascending.
     */
    public List<Alert> getAlerts() {
        // instead of previous getAllAlerts
        List<Alert> alerts = alert_repo.findAllByOrderByAlertTimeAsc();

        if (alerts.isEmpty()) {
            System.out.println("No Alerts found in the database");
        }
        return alerts;
    }

    /*
     * Finds a specific alert by its ID and wraps the
     *      result in an HTTP ResponseEntity.
     *
     * @param id The primary key ID of the alert.
     * @return 200 OK with the Alert body if found, or
     *      404 Not Found if it does not exist.
     */
    public ResponseEntity<Alert> findById(int id) {
        return alert_repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
     * Checks whether an alert with the given ID exists in the database.
     *
     * @param id The primary key ID of the alert.
     * @return true if the alert exists, false otherwise.
     */
    public boolean existsById(Integer id) {
        return alert_repo.existsById(id);
    }

    /*
     * Deletes an alert record by its primary key ID.
     *
     * @param id The primary key ID of the alert to delete.
     */
    public void deleteById(Integer id) {
        alert_repo.deleteById(id);
    }

    /*
     * Creates and saves a new alert linked to a Track entity
     *      (extracts trackNumber if track is not null).
     *
     * @param track  The Track associated with the alert (can be null).
     * @param result The ValidationResult containing the alert message and severity.
     */
    public void createAlert(Track track, ValidationResult result) {
        String trackNumber = null;
        // Malformed UDP messages may not have a Track
        if (track != null) {
            trackNumber = track.getTrackNumber();
        }
        Alert alert = new Alert(
                trackNumber,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        // call saveAlert in AlertService
        saveAlert(alert);
    }

    /*
     * Creates and saves a new alert using a raw track number string.
     *
     * @param trackNo The track number string associated with the alert.
     * @param result  The ValidationResult containing the alert message and severity.
     */
    public void createAlert(String trackNo, ValidationResult result) {
        Alert alert = new Alert(
                trackNo,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        // call saveAlert in AlertService
        saveAlert(alert);
    }

    /*
     * Creates and saves a general alert without an associated track number.
     *
     * @param result The ValidationResult containing the alert message and severity.
     */
    public void createAlert(ValidationResult result) {
        Alert alert = new Alert(
                null,
                result.getMessage(),
                result.getSeverity(),
                LocalDateTime.now()
        );
        // call saveAlert in AlertService
        saveAlert(alert);
    }
}