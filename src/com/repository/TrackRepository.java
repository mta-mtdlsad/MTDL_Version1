package com.repository;

import com.domain.Track;
import com.util.DbConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TrackRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;
    private List<Track> listOfTracks = null;

    // INSERT new tracks
    public Boolean saveTrack(Track track) throws Exception{
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO tracks (source_id,sequence_number,track_number,latitude,longitude,speed,identity_type,track_status,created_time) VALUES (?,?,?,?,?,?,?,?,?);");

            preparedStatement.setString(1, track.getSource_id());
            preparedStatement.setInt(2, track.getSequence_number());
            preparedStatement.setString(3, track.getTrack_number());
            preparedStatement.setDouble(4, track.getLatitude());
            preparedStatement.setDouble(5, track.getLongitude());
            preparedStatement.setDouble(6, track.getSpeed());
            preparedStatement.setString(7, track.getIdentity_type());
            preparedStatement.setString(8, track.getTrack_status());
            preparedStatement.setTimestamp(9, Timestamp.valueOf(track.getCreated_time()));

            return preparedStatement.executeUpdate() == 1;
        }catch (Exception e) {
            System.out.println("Failed to add track");
            throw e;
        }
        //return null;
    }

    //
    public void saveValidTracksAsTransaction(List<Track> tracks) throws Exception {
        connection.setAutoCommit(false);
        try {
            for(Track track : tracks) {
                saveTrack(track);
            }
            connection.commit();
            System.out.println("Track batch added successfully");
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }
    }

    // SELECT general function that returns a list based on selected statement
    public List<Track> getList(ResultSet resultSet) throws SQLException {
        try {
            listOfTracks = new ArrayList<>();
            while(resultSet.next()) {
                listOfTracks.add(
                        new Track(resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getInt(3),
                                resultSet.getString(4),
                                resultSet.getDouble(5),
                                resultSet.getDouble(6),
                                resultSet.getDouble(7),
                                resultSet.getString(8),
                                resultSet.getString(9),
                                resultSet.getTimestamp(10).toLocalDateTime()));
            }

            return listOfTracks;
        }catch (Exception e) {
           throw e;
        }
    }

    // SELECT to retrieve all tracks by the created time and the track_id
    public List<Track> getAllTracks() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks ORDER BY created_time, track_id;");
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT all tracks where the identity_type is HOSTILE
    public List<Track> getAllHostileTracks() {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE identity_type = 'HOSTILE' ORDER BY created_time;");
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT tracks where speed is greater than certain number
    public List<Track> getTracksBySpeed(Double speed) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE speed > ? ORDER BY speed DESC;");
            preparedStatement.setDouble(1, speed);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT track based on status
    public List<Track> getTracksByStatus(String status) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE track_status = ? ORDER BY created_time DESC, track_id DESC;");
            preparedStatement.setString(1, status);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT to retrieve all tracks in the supplied timeframe
    public List<Track> getAllTracksTimeframe(LocalDateTime start_time, LocalDateTime end_time) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE created_time >= ? AND created_time < ? ORDER BY created_time, track_id;");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start_time));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end_time));
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT tracks where speed is greater than certain number
    public List<Track> getTracksByIdentity(String identity) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE identity_type = ? ORDER BY created_time DESC, track_id DESC;");
            preparedStatement.setString(1, identity);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getList(resultSet);
        }catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // SELECT track based on the track_number
    public Track getTrackByNo(String number) {
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM tracks WHERE track_number = ? ORDER BY created_time DESC, track_id DESC LIMIT 1;");
            preparedStatement.setString(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();

            Track track;

            if(resultSet.next()) {
                track = new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getDouble(5),
                        resultSet.getDouble(6),
                        resultSet.getDouble(7),
                        resultSet.getString(8),
                        resultSet.getString(9),
                        resultSet.getTimestamp(10).toLocalDateTime());
                return track;
            }
        }catch (Exception e) {
            System.out.println("Unable to fetch track");
        }
        return null;
    }

    // SELECT the number of tracks in each identity_type
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> statistics = new LinkedHashMap<>();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT identity_type, COUNT(*) FROM tracks GROUP BY identity_type;");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> trackStatistics = new ArrayList<>();
            while(resultSet.next()) {
                String identity_type = resultSet.getString(1);
                int count = resultSet.getInt(2);
                statistics.put(identity_type, count);
            }

        }catch (Exception e) {
            System.out.println("Unable to fetch track statistics: " + e.getMessage());
        }
        return statistics;
    }

    // DELETE by the track_number
    public void deleteTrackByNo(String number) {
        try {
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM tracks WHERE track_number=?;");
            preparedStatement.setString(1, number);
            preparedStatement.executeUpdate();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE all tracks
    public boolean clearTracksTable() {
        try {
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM tracks;");
            preparedStatement.executeUpdate();
            return true;
        }catch(Exception e) {
            return false;
        }
    }
}
