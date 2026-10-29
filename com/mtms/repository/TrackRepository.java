package com.mtms.repository;

import com.mtms.domain.Track;
import com.mtms.util.DbConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrackRepository {

    // Saves the valid track to the tracks table
    public void insert(Track track) {
        //off (implemented on bacth)
        String sql = "INSERT INTO tracks (track_number, latitude, longitude, speed, identity_type, track_status, created_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, CAST(? AS TIMESTAMP))";

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, track.track_number);
            pstmt.setDouble(2, track.latitude);
            pstmt.setDouble(3, track.longitude);
            pstmt.setDouble(4, track.speed);
            pstmt.setString(5, track.identity_type);
            pstmt.setString(6, track.track_status);
            pstmt.setString(7, track.created_time);

            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    track.track_id = keys.getLong(1);
                }
            }
            //explicit on (implemented in batch)
            System.out.println("Track inserted; no Alert. Database assigned ID: " + track.track_id);

        } catch (SQLException e) {
            // try-catch block to handle TC-16 (Database unavailable)
            throw new RuntimeException("Database error during insert operation.", e);
        }
    }

    // Fetches all tracks stored in the database
    public List<Track> selectAll() {
        return executeQuery("SELECT * FROM tracks ORDER BY created_time, track_id", pstmt -> {});
    }

    public int deleteByTrackNumber(String trackNumber) {
        String sql = "DELETE FROM tracks WHERE track_number = ?";

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackNumber);

            // executeUpdate() returns the INT count of affected rows
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error during deletion query.", e);
        }
    }

    // TC-11 & TC-17: Secure parameterized search to handle text lookup and block SQL Injection
    public List<Track> search(String trackNumber) {
        String sql = "SELECT * FROM tracks WHERE track_number = ? ORDER BY created_time DESC, track_id DESC";
        return executeQuery(sql, pstmt -> pstmt.setString(1, trackNumber));
    }

    // TC-13: Filters and pulls records matching HOSTILE
    public List<Track> findByIdentity(String identity) {
        String sql = "SELECT * FROM tracks WHERE identity_type = ? ORDER BY created_time";
        return executeQuery(sql, pstmt -> pstmt.setString(1, identity.toUpperCase()));
    }

    // TC-14: Filters and pulls a target exceeding a target speed 700
    public List<Track> findHighSpeedTracks(double thresholdSpeed) {
        String sql = "SELECT * FROM tracks WHERE speed > ? ORDER BY speed DESC";
        return executeQuery(sql, pstmt -> pstmt.setDouble(1, thresholdSpeed));
    }

    // TC-15: Replay data
    public List<Track> getReplayData() {
        String sql = "SELECT * FROM tracks ORDER BY created_time, track_id";
        return executeQuery(sql, pstmt -> {});
    }

    // Functional interface to bind parameters
    private interface ParameterBinder {
        void bind(PreparedStatement pstmt) throws SQLException;
    }

    // Filters and pulls records falling within a specific time range.
    // Expects parameters formatted as 'yyyy-MM-dd HH:mm:ss'.
    public List<Track> selectByTimeWindow(String start, String end) {
        String sql = "SELECT * FROM tracks WHERE created_time BETWEEN CAST(? AS TIMESTAMP) AND CAST(? AS TIMESTAMP) ORDER BY created_time, track_id";
        return executeQuery(sql, pstmt -> {
            pstmt.setString(1, start);
            pstmt.setString(2, end);
        });
    }

    // Filters and retrieves Active or Inactive tracks
    public List<Track> selectByStatus(String status) {
        String sql = "SELECT * FROM tracks WHERE UPPER(track_status) = ? ORDER BY created_time, track_id";
        return executeQuery(sql, pstmt -> pstmt.setString(1, status.trim().toUpperCase()));
    }


    // Batch Proccessing starts
    public void insertBatch(List<Track> validTracks) {
        String sql = "INSERT INTO tracks (track_number, latitude, longitude, speed, identity_type, track_status, created_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, CAST(? AS TIMESTAMP))";

        try (Connection conn = DbConnectionUtil.getConnection()) {
            // Disable auto-commit to start a transaction
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Track track : validTracks) {
                    pstmt.setString(1, track.track_number);
                    pstmt.setDouble(2, track.latitude);
                    pstmt.setDouble(3, track.longitude);
                    pstmt.setDouble(4, track.speed);
                    pstmt.setString(5, track.identity_type);
                    pstmt.setString(6, track.track_status);
                    pstmt.setString(7, track.created_time);

                    // Add the record to the in-memory batch queue
                    pstmt.addBatch();
                }

                // Transmit the entire collection over a single pipeline request
                pstmt.executeBatch();

                // Commit the transaction to save all records at once
                conn.commit();
                System.out.println("Batch transaction committed successfully!");

            } catch (SQLException e) {
                // Rollback completely if an error occurs
                conn.rollback();
                throw new RuntimeException("Batch insertion error", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connectivity failure encountered during batch transaction.", e);
        }
    }
    // Batch Proccessing ends




    // Helper method to keep data execution logic simple
    private List<Track> executeQuery(String sql, ParameterBinder binder) {
        List<Track> list = new ArrayList<>();

        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            binder.bind(pstmt);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Track(
                            rs.getLong("track_id"),
                            rs.getString("track_number"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getDouble("speed"),
                            rs.getString("identity_type"),
                            rs.getString("track_status"),
                            rs.getString("created_time")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database connectivity failure encountered during query operation.", e);
        }
        return list;
    }
}
