package dao;

import model.Actor;
import model.Category;
import model.Dvd;
import model.Media;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DvdDAO {
    private final ActorDAO actorDao;
    private final CategoryDAO categoryDao;
    private final Connection connection;
    private List<Dvd> dvdList;

    public DvdDAO(ActorDAO actorDao, CategoryDAO categoryDao, Connection connection){
        this.actorDao = actorDao;
        this.categoryDao = categoryDao;
        this.connection = connection;
        this.dvdList = new ArrayList<>();
    }

    public void loadDvdList() {
        this.dvdList = getAllDvd();
    }

    public List<Dvd> getAllDvd() {
        List<Dvd> dvdResults = new ArrayList<>();
        String sql = "SELECT * FROM Media WHERE mediaType = 'DVD'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int mediaId = rs.getInt("mediaId");
                String mediaName = rs.getString("mediaName");
                Media.MediaType mediaType = Media.MediaType.valueOf(rs.getString("mediaType"));
                int ageLimit = rs.getInt("ageLimit");
                String productionCountry = rs.getString("productionCountry");
                String director = rs.getString("director");
                int duration = rs.getInt("duration");
                boolean partOfCourse = rs.getBoolean("partOfCourse");

                Dvd currentDvd = new Dvd(
                        mediaId,
                        mediaName,
                        mediaType,
                        partOfCourse,
                        ageLimit,
                        productionCountry,
                        director,
                        duration
                );

                List<Actor> actors = actorDao.getActorsByMediaId(mediaId);
                List<Category> categories = categoryDao.getAllCategoriesByMediaId(mediaId);

                if (actors != null) currentDvd.setActor(actors);
                if (categories != null) currentDvd.setCategory(categories);

                dvdResults.add(currentDvd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dvdResults;
    }

    public List<Dvd> searchDvdByDuration(int maxDuration) {
        List<Dvd> results = new ArrayList<>();
        if (dvdList == null || dvdList.isEmpty()) {
            loadDvdList();
        }
        for (Dvd dvd : dvdList) {
            if (dvd.getDuration() <= maxDuration) {
                results.add(dvd);
            }
        }
        return results;
    }
    public void updateDvd(int mediaId, boolean partOfCourse) throws SQLException {
        String sql = "UPDATE Media set partOfCourse = ? WHERE mediaId = ? AND mediaType = 'DVD'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, partOfCourse);
            stmt.setInt(2, mediaId);
            stmt.executeUpdate();
        }
    }
    public int addDvdAndReturnMediaId(Dvd dvd) throws SQLException {
        String sql = "INSERT INTO Media (mediaName, mediaType, ageLimit, productionCountry, director, duration, partOfCourse) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dvd.getMediaName());
            stmt.setString(2, dvd.getMediaType().toString());
            stmt.setInt(3, dvd.getAgeLimit());
            stmt.setString(4, dvd.getProductionCountry());
            stmt.setString(5, dvd.getDirector());
            stmt.setInt(6, dvd.getDuration());
            stmt.setBoolean(7, dvd.getPartOfCourse());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Misslyckades att hämta genererat mediaId.");
                }
            }
        }
    }

    public void addDvd(Dvd dvd) throws SQLException {
        String sql = "INSERT INTO Media (mediaName, mediaType, partOfCourse, ageLimit, productionCountry, director, duration) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, dvd.getMediaName());
            pstmt.setString(2, dvd.getMediaType().toString());
            pstmt.setBoolean(3, dvd.getPartOfCourse());
            pstmt.setInt(4, dvd.getAgeLimit());
            pstmt.setString(5, dvd.getProductionCountry());
            pstmt.setString(6, dvd.getDirector());
            pstmt.setInt(7, dvd.getDuration());

            pstmt.executeUpdate();
        }
    }
}