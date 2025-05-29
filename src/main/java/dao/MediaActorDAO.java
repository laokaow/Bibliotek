package dao;

import model.MediaActor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaActorDAO {

    private final Connection connection;

    public MediaActorDAO(Connection connection) {
        this.connection = connection;
    }

    public void addMediaActor(int mediaId, int actorId) throws SQLException {
        String sql = "INSERT INTO MediaActor (mediaId, actorId) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.setInt(2, actorId);
            stmt.executeUpdate();
        }
    }

    public List<MediaActor> findByMediaId(int mediaId) throws SQLException {
        List<MediaActor> result = new ArrayList<>();
        String sql = "SELECT mediaActorId, mediaId, actorId FROM MediaActor WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int mediaActorId = rs.getInt("mediaActorId");
                    int actorId = rs.getInt("actorId");
                    result.add(new MediaActor(mediaActorId, mediaId, actorId));
                }
            }
        }
        return result;
    }

    public void removeAllActorsFromMedia(int mediaId) throws SQLException {
        String sql = "DELETE FROM MediaActor WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.executeUpdate();
        }
    }
    public void deleteByMediaId(int mediaId) throws SQLException {
        String sql = "DELETE FROM MediaActor WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.executeUpdate();
        }
    }
    public void deleteById(int mediaActorId) throws SQLException {
        String sql = "DELETE FROM MediaActor WHERE mediaActorId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaActorId);
            stmt.executeUpdate();
        }
    }
}