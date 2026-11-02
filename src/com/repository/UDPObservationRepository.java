package com.repository;

import com.network.UDPMessage;
import com.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UDPObservationRepository {
//    private static Connection connection = DbConnection.getConnection();
//    private PreparedStatement stmt = null;

    public boolean saveObservation(UDPMessage message) throws Exception {
        // SQL statement to insert a new observation
        String sql = "INSERT INTO udp_observations (source_id,sequence_number,track_number,latitude,longitude,speed,identity,status,observation_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (source_id, sequence_number) DO NOTHING ";
        // Open connection using your existing database class
        try (Connection connection = DbConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql);){
            // Set observation details
            stmt.setString(1, message.sourceId());
            stmt.setInt(2, message.sequenceNumber());
            stmt.setString(3, message.trackNumber());
            // Set position and speed
            stmt.setDouble(4, message.latitude());
            stmt.setDouble(5, message.longitude());
            stmt.setDouble(6, message.speed());
            // Set identity and status
            stmt.setString(7, message.identity());
            stmt.setString(8, message.status());
            // Convert LocalDateTime to SQL Timestamp
            stmt.setTimestamp(9, Timestamp.valueOf(message.observationTime()));
            // True if inserted, false if duplicate
            return stmt.executeUpdate() == 1;
        }catch (Exception ex){
            System.out.println("Failed to add observation");
            throw ex;
        }
    }
}
