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
    List<Category> categoryList = new ArrayList<>();
    public List<Category> getAllCategories() throws SQLException {
       List<Category> categories = new ArrayList<>();
        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "select * from Category";
    try {
        con = DatabaseConnection.getConnection();
        preparedStatement = con.prepareStatement(sql);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Category category = new Category(
                    resultSet.getInt("categoryId"),
                    resultSet.getString("categoryName")
            );
            categories.add(category);
        }
    }
    catch (SQLException e) {
        e.printStackTrace();
    }
    finally {
          if(resultSet != null) resultSet.close();
          if(preparedStatement != null) preparedStatement.close();
          if(con != null) con.close();
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

