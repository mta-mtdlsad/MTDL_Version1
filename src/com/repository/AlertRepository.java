package com.repository;

import com.domain.Alert;
import com.domain.Track;
import com.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AlertRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;
    private List<Alert> listOfAlerts = null;

    //INSERT
    public void saveAlert(Alert alert) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO alerts (track_number,alert_message,alert_severity,alert_time) VALUES (?,?,?,?);");

            preparedStatement.setString(1, alert.getTrack_number());
            preparedStatement.setString(2, alert.getAlert_message());
            preparedStatement.setString(3, alert.getAlert_severity());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(alert.getAlert_time()));

            preparedStatement.executeUpdate();
            System.out.println("Alert added successfully");
        }catch (Exception e) {
            System.out.println("Failed to add alert");
        }
    }

    // SELECT
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
}
