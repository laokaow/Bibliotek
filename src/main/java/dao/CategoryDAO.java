package dao;

import model.Category;
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


    public List<Category> getAllCategoriesByMediaId(int mediaId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.categoryId, c.categoryName " +
                "FROM MediaCategory mc " +
                "JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE mc.mediaId = ?";

        try(Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
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

