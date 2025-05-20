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

    public void loadData() throws SQLException {
            try {
                mediaList = getMedia();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

    }
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
        for(Media media : mediaList) {
            if(media.getMediaName().toLowerCase().contains(userInput.toLowerCase())) {
                results.add(media);
            }
        }
        return results;
    }
    public List<Media> searchByTitle(String userInput) throws SQLException {
       List <Media> results = new ArrayList<>();
       for(Media media : mediaList) {
           if(media.getMediaName().toLowerCase().contains(userInput.toLowerCase())) {
               results.add(media);
           }
    }
       return results;

    }
    public List<Media> searchByType(String userInput) throws SQLException {
    List <Media> results = new ArrayList<>();
    for(Media media : mediaList) {
        if(media.getMediaType().toString().toLowerCase().contains(userInput.toLowerCase())) {
            results.add(media);
        }
    }
    return results;
    }

    public List<Media> searchByCategory(String userInput) throws SQLException {
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
            String sql = "SELECT * FROM media WHERE media_category LIKE ?";
            connection = DatabaseConnection.getConnection();
            ps = connection.prepareStatement(sql);
             ps.setString(1, "%" + userInput + "%");
        return null;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
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
    public Media getMediaById(int mediaId) throws SQLException {
        String sql = "SELECT * FROM Media WHERE mediaId = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, mediaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("mediaId");
                    String title = rs.getString("title");
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
//      Connection connection = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        try {
//            connection = DatabaseConnection.getConnection();
//            String sql2 = "SELECT * FROM media WHERE media_type LIKE ?";
//             ps = connection.prepareStatement(sql2);
//            ps.setString(1, "%" + userInput + "%");
//            return null;
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//        finally {
//            if(ps != null) ps.close();
//            if(connection != null) connection.close();
//            if(rs != null) rs.close();
//
//        }