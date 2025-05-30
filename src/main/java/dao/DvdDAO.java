package dao;

import model.Actor;
import model.Category;
import model.Dvd;
import model.Media;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DvdDAO {
    private final ActorDAO actorDao;
    private final CategoryDAO categoryDao;
    private final Connection connection;

    public DvdDAO(ActorDAO actorDao, CategoryDAO categoryDao, Connection connection) {
        this.actorDao = actorDao;
        this.categoryDao = categoryDao;
        this.connection = connection;
    }

    // Gjort detta för att slippa skriva rs.getX 1000 gånger...
    private Dvd buildDvd(ResultSet rs) throws SQLException {
        return new Dvd(
                rs.getInt("mediaId"),
                rs.getString("mediaName"),
                Media.MediaType.valueOf(rs.getString("mediaType")),
                rs.getBoolean("partOfCourse"),
                rs.getInt("ageLimit"),
                rs.getString("productionCountry"),
                rs.getString("director"),
                rs.getInt("duration"),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    /**
     * sammas sak som ovan agerar som en "Helper" klass, eftersom samma sak ska göras flertalet gånger.
     * Så är det snyggare och och innebär mindre repetetiv kod att skapa en metod som gör det åt oss.
     */
    private void addCategoryAndActorToDvd(ResultSet rs, Dvd dvd) throws SQLException {
        int categoryId = rs.getInt("categoryId");
        String categoryName = rs.getString("categoryName");

        if (categoryId != 0 && categoryName != null) {
            Category category = new Category(categoryId, categoryName);
            dvd.addCategory(category);
        }
        int actorId = rs.getInt("actorId");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");

        if (actorId != 0 && firstName != null && lastName != null) {
            Actor actor = new Actor(actorId, firstName, lastName);
            dvd.addActor(actor);
        }
    }

    public List<Dvd> getAllDvd() throws SQLException {
        List<Dvd> dvdResults = new ArrayList<>();
        String sql = "SELECT m.mediaId, m.mediaName, m.ageLimit, m.mediaType, m.partOfCourse, " +
                "m.productionCountry, m.director, m.duration, " +
                "a.actorId, a.firstName, a.lastName, " +
                "c.categoryId, c.categoryName " +
                "FROM Media m " +
                "LEFT JOIN MediaActor ma ON m.mediaId = ma.mediaId " +
                "LEFT JOIN Actor a ON ma.actorId = a.actorId " +
                "LEFT JOIN MediaCategory mc ON m.mediaId = mc.mediaId " +
                "LEFT JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE m.mediaType = 'DVD' " +
                "ORDER BY m.mediaId";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            Map<Integer, Dvd> dvdMap = new HashMap<>();
            while (rs.next()) {
                int mediaId = rs.getInt("mediaId");

                Dvd dvd = dvdMap.get(mediaId);
                if (dvd == null) {
                    dvd = buildDvd(rs);
                    dvdMap.put(mediaId, dvd);
                }
                addCategoryAndActorToDvd(rs, dvd);

            }
            dvdResults.addAll(dvdMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dvdResults;
    }

    public List<Dvd> searchDvdByDuration(int duration) throws SQLException {
        List<Dvd> results = new ArrayList<>();

        String sql = "SELECT m.mediaId, m.mediaName, m.mediaType, m.partOfCourse, m.ageLimit, " +
                "m.productionCountry, m.director, m.duration, " +
                "a.actorId, a.firstName, a.lastName, " +
                "c.categoryId, c.categoryName " +
                "FROM Media m " +
                "LEFT JOIN MediaActor ma ON m.mediaId = ma.mediaId " +
                "LEFT JOIN Actor a ON ma.actorId = a.actorId " +
                "LEFT JOIN MediaCategory mc ON m.mediaId = mc.mediaId " +
                "LEFT JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE m.mediaType = 'DVD' AND m.duration <= ? " +
                "ORDER BY m.mediaId";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, duration);

            try (ResultSet rs = stmt.executeQuery()) {
                Map<Integer, Dvd> dvdMap = new HashMap<>();
                while (rs.next()) {
                    int mediaId = rs.getInt("mediaId");
                    Dvd dvd = dvdMap.get(mediaId);
                    //om dvd:n inte finns i mapen skapa då en ny och lägg till den.
                    if (dvd == null) {
                        dvd = buildDvd(rs);
                        /**
                         *  Lägger till dvd:n med mediaId som nyckel.
                         * */
                        dvdMap.put(mediaId, dvd);
                    }
                    // lägger till kategorier och skådespelare till dvd:en
                    addCategoryAndActorToDvd(rs, dvd);
                    /**
                     * hämtar categoryId:et från rs.Om resultsetet returnerade något dvs notNull och att categoryId inte är 0
                     * Så skapas ett nytt category objekt.
                     * Om den nya kategorin/genre inte matchar med en redan existerande kategori i objektet,
                     * lägg då till den till dvd:en. Annars gör inget.
                     */


                }
                // lägger till alla dvd in i results arrayListan. Kommer bara vara unika DVD objekt.
                results.addAll(dvdMap.values());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return results;
        }
    }

    public List<Dvd> searchByDirector (String director) throws SQLException {
        List<Dvd> results = new ArrayList<>();
        String sql = "SELECT m.mediaId, m.mediaName, m.mediaType, m.partOfCourse, m.ageLimit, " +
                "m.productionCountry, m.director, m.duration, " +
                "a.actorId, a.firstName, a.lastName, " +
                "c.categoryId, c.categoryName " +
                "FROM Media m " +
                "LEFT JOIN MediaActor ma ON m.mediaId = ma.mediaId " +
                "LEFT JOIN Actor a ON ma.actorId = a.actorId " +
                "LEFT JOIN MediaCategory mc ON m.mediaId = mc.mediaId " +
                "LEFT JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE m.mediaType = 'DVD' AND LOWER(m.director) LIKE ? " +
                "ORDER BY m.mediaId";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1,"%" + director.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                Map<Integer, Dvd> dvdDirectorMap = new HashMap<>();
                while (rs.next()) {
                    int mediaId = rs.getInt("mediaId");
                    Dvd dvd = dvdDirectorMap.get(mediaId);
                    if (dvd == null) {
                        dvd = buildDvd(rs);
                        dvdDirectorMap.put(mediaId, dvd);
                    }
                    addCategoryAndActorToDvd(rs, dvd);
                }
                results.addAll(dvdDirectorMap.values());
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {

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