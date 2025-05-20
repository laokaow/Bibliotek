package dao;

import model.Actor;
import model.Category;
import model.Dvd;
import model.Media;
import util.DatabaseConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DvdDAO {
   private ActorDAO actorDao;
   private CategoryDAO categoryDao;
   private Connection connection;

   public DvdDAO(ActorDAO actorDao, CategoryDAO categoryDao, Connection connection){
       this.actorDao = actorDao;
       this.categoryDao = categoryDao;
       this.connection = connection;
   }

    List<Dvd> dvdList;
    public DvdDAO(ActorDAO actorDao, CategoryDAO categoryDao) {
        this.actorDao = actorDao;
        this.categoryDao = categoryDao;
    }
    public void loadDvdList() {
        dvdList = getAllDvd();
    }
    public List<Dvd> getAllDvd() {
        List<Dvd> dvdResults = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM Media WHERE mediaType = 'DVD'";
        try(Connection conn = DatabaseConnection.getConnection()) {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            //Dvd currentDvd = null;
            //int lastMediaId = -1;
            while(rs.next()) {
                int mediaId = rs.getInt("mediaId");
                String mediaName = rs.getString("mediaName");
                Media.MediaType mediaType = Media.MediaType.valueOf(rs.getString("mediaType"));
                int ageLimit = rs.getInt("ageLimit");
                String productionCountry = rs.getString("productionCountry");
                String director = rs.getString("director");
                int duration = rs.getInt("duration");
                boolean partOfCourse = rs.getBoolean("partOfCourse");
            /*
                if(currentDvd == null || lastMediaId != mediaId) {
                    if(currentDvd != null) {
                        dvd.add(currentDvd);
                    }
                }
                currentDvd */
                Dvd currentDvd = new Dvd(
                        mediaId,
                        mediaName,
                        mediaType,
                        partOfCourse,
                        ageLimit,
                        productionCountry,
                        director,
                        duration);
                List <Actor> actors = actorDao.getActorsByMediaId(mediaId);
                List <Category> categories = categoryDao.getAllCategoriesByMediaId(mediaId);
                if(actors != null) {
                    currentDvd.setActor(actors);
                }
                if(categories != null) {
                    currentDvd.setCategory(categories);
                }
                dvdResults.add(currentDvd);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return dvdResults;
    }

    public List<Dvd> searchDvdByDuration(int duration) throws SQLException {
        try {
            List<Dvd> results = new ArrayList<>();
            for(Dvd dvd : dvdList) {
                if(dvd.getDuration() <= duration) {
                    results.add(dvd);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return dvdList;
    }
    public void addDvd(Dvd dvd) throws SQLException {
        String sql = "INSERT INTO Media (mediaName, mediaType, partOfCourse, ageLimit, productionCountry, director, duration) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection con = DatabaseConnection.getConnection();
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
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
 }
