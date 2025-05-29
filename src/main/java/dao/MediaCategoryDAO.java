package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaCategoryDAO {
    private Connection connection;

    public MediaCategoryDAO(Connection connection) {
        this.connection = connection;
    }

    public void addCategoryToMedia(int mediaId, int categoryId) throws SQLException {
        String sql = "INSERT INTO MediaCategory (mediaId, categoryId) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }
    public void deleteCategoriesForMedia(int mediaId) throws SQLException {
        String sql = "DELETE FROM MediaCategory WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.executeUpdate();
        }
    }

    public void removeAllCategoriesFromMedia(int mediaId) throws SQLException {
        String sql = "DELETE FROM MediaCategory WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getCategoryIdsForMedia(int mediaId) throws SQLException {
        List<Integer> categoryIds = new ArrayList<>();
        String sql = "SELECT categoryId FROM MediaCategory WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categoryIds.add(rs.getInt("categoryId"));
                }
            }
        }
        return categoryIds;
    }
}