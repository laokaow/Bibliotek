package dao;

import model.Media;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {
    List<Media> mediaList = new ArrayList<Media>();

private final Connection connection;

    public MediaDAO(Connection connection) {
        this.connection = connection;
    }


    public void deleteMedia(int mediaId) throws SQLException {
        String sql = "DELETE FROM Media WHERE mediaId = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mediaId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Ingen media hittades med ID: " + mediaId);
            }
        }
    }
    //Gammalt implementerad metod
    //Den får ligga kvar för att visa att mer än ett sätt funkar
    public List<Media> getMedia() throws SQLException {
        List<Media> list = new ArrayList<Media>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from Media";
        try {
            con = DatabaseConnection.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Media media = new Media(
                        rs.getInt("mediaId"),
                        rs.getString("mediaName"),
                        Media.MediaType.valueOf(rs.getString("mediaType").toUpperCase()),
                        rs.getBoolean("partOfCourse")
                );
                list.add(media);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<Media> searchMedia(String userInput) {
        List<Media> results = new ArrayList<>();
        String sql = "select * from Media where mediaName like ?";
        try( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + userInput.toLowerCase() + "%" );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public int getLoanPeriod(Media.MediaType mediaType) throws SQLException { //Hämtar lånperioden för de olika sorternas media
        String sql = "SELECT getLoanPeriod(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(mediaType));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("getLoanPeriod returnerade inget.");
        }
    }

    public void updatePartOfCourse(int mediaId, boolean partOfCourse) throws SQLException {
        String sql = "UPDATE Media SET partOfCourse = ? WHERE mediaId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, partOfCourse);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        }
    }

    public void insertMediaCategory(int mediaId, int categoryId) throws SQLException {
        String sql = "INSERT INTO MediaCategory (mediaId, categoryId) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }
    public Media getMediaById(int mediaId) throws SQLException {
        String sql = "SELECT * FROM Media WHERE mediaId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, mediaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("mediaId");
                    String title = rs.getString("mediaName");
                    Media.MediaType mediaType = Media.MediaType.valueOf(rs.getString("mediaType"));
                    Boolean partOfCourse = rs.getBoolean("partOfCourse");

                    return new Media(id, title, mediaType, partOfCourse);
                } else {
                    return null;  // Eller kasta ett undantag om mediaId inte finns
                }
            }
        }
    }
}
