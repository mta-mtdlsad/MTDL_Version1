package com.mtms.repository;

import com.mtms.domain.Track;
import com.mtms.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TrackRepository {
    private static Connection connection = DbConnection.getConnection();
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private List<Track> listOfTracks = null;

    // Adds a track to the database
    public void addTrack(Track track) {
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO tracks (track_number, latitude, longtitude, speed, identity_type, track_status, created_time)"+"VALUES (?,?,?,?,?,?,?)");
            preparedStatement.setString(1, track.getTrackNumber());
            preparedStatement.setDouble(2, track.getLatitude());
            preparedStatement.setDouble(3, track.getLongitude());
            preparedStatement.setFloat(4, track.getSpeed());
            preparedStatement.setString(5, track.getIdentity());
            preparedStatement.setString(6, track.getTrackStatus());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(track.getCreatedTime()));
            preparedStatement.executeUpdate();
            System.out.println("Track added successfully");
        } catch (Exception e) {
            System.out.println("Track are not added"+ e);
        }
    }

    // Retrieves all tracks from the database
    public List<Track> getAllTracks(){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks");
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // Retrieves tracks by its track number from the database
    public List<Track> getTrackByNumber(String trackNumber){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE track_number=? ORDER BY created_time DESC, track_id DESC LIMIT 1");
            preparedStatement.setString(1, trackNumber);
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // Deletes a track by its track number in the database
    public Boolean deleteTrackByNumber(String trackNumber){
        try{
            preparedStatement = connection.prepareStatement("DELETE FROM tracks WHERE track_number=?");
            preparedStatement.setString(1, trackNumber);
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            System.out.println("Track " + trackNumber + " cannot be deleted");
        }
        return false;
    }

    // Orders the tracks by the time its created
    public List<Track> tracksOrderedByTime(){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks ORDER BY created_time, track_id");

            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch all tracks");
        }
        return null;
    }

    // Retrieves hostile tracks
    public List<Track> getHostileTracks(){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE identity_type = 'HOSTILE' ORDER BY created_time");
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch hostile tracks");
        }
        return null;
    }

    // Retrieves all tracks with high speed (>700)
    public List<Track> getHighSpeedTracks(){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE speed > 700 ORDER BY speed DESC");
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch high speed tracks");
        }
        return null;
    }

    // Counts the track's identity type using SQL GROUP BY
    public Map<String, Integer> getIdentityCount() {
        Map<String, Integer> IdentityCount = new LinkedHashMap<>();

        try {
            preparedStatement = connection.prepareStatement("SELECT identity_type, COUNT(*) FROM tracks GROUP BY identity_type");
            ResultSet resultSetAlert = preparedStatement.executeQuery();

            while (resultSetAlert.next()) {
                String identity_type = resultSetAlert.getString(1);
                Integer identity_Count = resultSetAlert.getInt(2);

                IdentityCount.put(identity_type, identity_Count);
            }

        } catch (Exception exs) {
            System.out.println("Unable to fetch alert severity details");
        }
        return IdentityCount;
    }

    // Retrieves active tracks
    public List<Track> getActiveTracks(){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE track_status = 'ACTIVE' ORDER BY created_time");
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch active tracks");
        }
        return null;
    }

    // Retrieves inactive tracks
    public List<Track> getInactiveTracks(){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE track_status = 'INACTIVE' ORDER BY created_time");
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch inactive tracks");
        }
        return null;
    }

    // Retrieves the tracks filtered by the start and end time, ordered by time and trackId
    public List<Track> tracksFilterdByTime(LocalDateTime startTime, LocalDateTime endTime) {
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE created_time BETWEEN ? AND ? ORDER BY created_time, track_id");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startTime));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endTime));
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch all tracks filtered by time" + e);
        }
        return null;
    }


    //Select to count tracks by identity () using SQL GROUP BY
    public Map<String, Integer> getTrackIdentity() {
        Map<String, Integer> identityCount = new LinkedHashMap<>();

        try{
            preparedStatement= connection.prepareStatement("SELECT identity_type, COUNT(*) FROM TRACKS GROUP BY identity_type");
            ResultSet resultSetAlert = preparedStatement.executeQuery();

            while (resultSetAlert.next()){
                String TrackIdentity = resultSetAlert.getString(1);
                Integer IdentityCount = resultSetAlert.getInt(2);

                identityCount.put(TrackIdentity, IdentityCount);
            }

        } catch (Exception exs) {
            System.out.println("Unable to fetch track identity details");
        }
        return identityCount;
    }

    // Retrieves tracks based on idinttiy
    public List<Track> getTracksByIdentity(String identityType){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE identity_type = ? ORDER BY created_time");
            preparedStatement.setString(1, identityType);
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch tracks by identity");
        }
        return null;
    }

    // Retrieves all above a minimum speed
    public List<Track> getTracksByMinimumSpeed(int minimumSpeed){
        try{
            preparedStatement = connection.prepareStatement("SELECT * FROM tracks WHERE speed > ? ORDER BY speed DESC");
            preparedStatement.setInt(1, minimumSpeed);
            resultSet = preparedStatement.executeQuery();
            listOfTracks = new ArrayList<Track>();
            while (resultSet.next()){
                listOfTracks.add(new Track(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getFloat(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getTimestamp(8).toLocalDateTime()
                ));
            }
            if (!listOfTracks.isEmpty())
                return listOfTracks;
        } catch (Exception e) {
            System.out.println("Unable to fetch tracks");
        }
        return null;
    }


}