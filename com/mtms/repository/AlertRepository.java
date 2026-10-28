package com.mtms.repository;

import com.mtms.domain.Alert;
import com.mtms.util.DbConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertRepository {

    // Saves validation alerts to the alerts table
    public void insert(Alert alert) {
        String sql = "INSERT INTO alerts (track_number, alert_message, alert_severity, alert_time) " +
                "VALUES (?, ?, ?, CAST(? AS TIMESTAMP))";

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, alert.track_number);
            pstmt.setString(2, alert.alert_message);
            pstmt.setString(3, alert.alert_severity);
            pstmt.setString(4, alert.alert_time);

            pstmt.executeUpdate();

            // maps generated primary key
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    alert.alert_id = keys.getLong(1);
                }
            }

        } catch (SQLException e) {
            // Throws up to catch block for TC-16 (Database unavailable)
            throw new RuntimeException("Database error during alert logging operation.", e);
        }
    }

    // Fetches all recorded validation alerts sorted by latest creation log
    public List<Alert> selectAll() {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM alerts ORDER BY alert_time DESC, alert_id DESC";

        try (Connection conn = DbConnectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alerts.add(new Alert(
                        rs.getLong("alert_id"),
                        rs.getString("track_number"),
                        rs.getString("alert_message"),
                        rs.getString("alert_severity"),
                        rs.getString("alert_time")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error during alert query operation.", e);
        }
        return alerts;
    }
}
