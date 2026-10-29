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
    public String saveTrack(Track track) throws Exception{
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO tracks (track_number,latitude,longitude,speed,identity_type,track_status,created_time) VALUES (?,?,?,?,?,?,?);");

            preparedStatement.setString(1, track.getTrack_number());
            preparedStatement.setDouble(2, track.getLatitude());
            preparedStatement.setDouble(3, track.getLongitude());
            preparedStatement.setDouble(4, track.getSpeed());
            preparedStatement.setString(5, track.getIdentity_type());
            preparedStatement.setString(6, track.getTrack_status());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(track.getCreated_time()));

            preparedStatement.executeUpdate();
            return "Track added successfully";
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
                                resultSet.getDouble(3),
                                resultSet.getDouble(4),
                                resultSet.getDouble(5),
                                resultSet.getString(6),
                                resultSet.getString(7),
                                resultSet.getTimestamp(8).toLocalDateTime()));
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
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getDouble(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime());
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
    public boolean deleteTrackByNo(String number) {
        try {
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM tracks WHERE track_number=?;");
            preparedStatement.setString(1, number);
            preparedStatement.executeUpdate();
            return true;
        }catch(Exception e) {
            return false;
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
