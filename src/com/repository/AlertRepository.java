package com.repository;

import com.domain.Alert;
import com.domain.Track;
import com.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlertRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;
    private List<Alert> listOfAlerts = null;

    //INSERT Statement to insert new alerts
    public String saveAlert(Alert alert) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO alerts (track_number,alert_message,alert_severity,alert_time) VALUES (?,?,?,?);");

            preparedStatement.setString(1, alert.getTrack_number());
            preparedStatement.setString(2, alert.getAlert_message());
            preparedStatement.setString(3, alert.getAlert_severity());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(alert.getAlert_time()));

            preparedStatement.executeUpdate();
            return "Alert added successfully";
        }catch (Exception e) {
            System.out.println("Failed to add alert");
        }
        return null;
    }

    // SELECT to retrieve all alerts by the alert_time
    public List<Alert> getAllAlerts() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM alerts ORDER BY alert_time;");
            ResultSet resultSet = preparedStatement.executeQuery();

            listOfAlerts = new ArrayList<Alert>();
            while(resultSet.next()) {
                listOfAlerts.add(
                        new Alert(resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getTimestamp(5).toLocalDateTime()));
            }

            return listOfAlerts;
        }catch (Exception e) {
            System.out.println("Unable to fetch all alerts");
        }
        return null;
    }

    // FR-1.1-09 :: Select to Count alerts by severity using SQL GROUP BY
    public Map<String, Integer> getAlertStatistics() {
        Map<String, Integer> statistics = new LinkedHashMap<>();

        String sql = "SELECT alert_severity, COUNT(*) AS total FROM alerts GROUP BY alert_severity";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String severity = rs.getString("alert_severity");
                int count = rs.getInt("total");

                statistics.put(severity, count);
            }

        } catch (Exception e) {
            System.out.println("Unable to fetch alert statistics: " + e.getMessage());
        }

        return statistics;
    }
}
