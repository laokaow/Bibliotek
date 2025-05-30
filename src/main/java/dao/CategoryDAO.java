package dao;

import model.Category;
import model.Media;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private Connection connection;

    public CategoryDAO(Connection connection){
        this.connection = connection;
    }


    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT categoryId, categoryName FROM Category";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("categoryId");
                String name = rs.getString("categoryName");
                categories.add(new Category(id, name));
            }
        }

        return categories;
    }
    public List<Media> getMediaByCategoryName(String categoryName) throws SQLException {
        List<Media> mediaList = new ArrayList<>();
        String sql = "SELECT m.mediaId, m.mediaName, m.mediaType, m.partOfCourse FROM Media m " +
                "JOIN MediaCategory mc ON m.mediaId = mc.mediaId " +
                "JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE c.categoryName = ? ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, categoryName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int mediaId = resultSet.getInt("mediaId");
                Media media = new Media(
                        mediaId,
                        resultSet.getString("mediaName"),
                        Media.MediaType.valueOf(resultSet.getString("mediaType")),
                        resultSet.getBoolean("partOfCourse")
                );
                mediaList.add(media);
            }
        }

        return mediaList;
    }

    public List<Category> getAllCategoriesByMediaId(int mediaId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.categoryId, c.categoryName " +
                "FROM MediaCategory mc " +
                "JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE mc.mediaId = ?";

        try(PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setInt(1, mediaId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
               int categoryId =  resultSet.getInt("categoryId");
               String categoryName = resultSet.getString("categoryName");

                Category category = new Category(
                        categoryId,
                        categoryName
                );
                categories.add(category);
            }
        }
        return categories;
    }
}

