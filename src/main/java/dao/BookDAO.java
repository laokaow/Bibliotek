package dao;

import model.Book;
import model.Category;
import model.Media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BookDAO {
    private Connection connection;
    private CategoryDAO categoryDao;

    public BookDAO(Connection connection, CategoryDAO categoryDao) {
        this.connection = connection;
        this.categoryDao = categoryDao;
    }

    private Book buildBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("mediaId"),
                rs.getString("mediaName"),
                Media.MediaType.valueOf(rs.getString("mediaType").toUpperCase()),
                rs.getBoolean("partOfCourse"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("pageCount"),
                new ArrayList<>()
        );
    }
    private void addCategory(ResultSet rs, Book book) throws SQLException {
        int categoryId = rs.getInt("categoryId");
        String categoryName = rs.getString("categoryName");

        if (categoryId != 0 && categoryName != null) {
            Category category = new Category(categoryId, categoryName);
            book.addCategory(category);
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT m.mediaId, m.mediaName, m.mediaType, m.partOfCourse, " +
                "m.author, m.ISBN, m.pageCount, " +
                "c.categoryId, c.categoryName " +
                "FROM Media m " +
                "LEFT JOIN MediaCategory mc ON m.mediaId = mc.mediaId " +
                "LEFT JOIN Category c ON mc.categoryId = c.categoryId " +
                "WHERE m.mediaType = 'Book' " +
                "ORDER BY m.mediaId";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Map<Integer, Book> bookMap = new TreeMap<>();
            while (resultSet.next()) {
                int mediaId = resultSet.getInt("mediaId");
                Book book = bookMap.get(mediaId);

                if(book == null) {
                    book = buildBook(resultSet);
                    bookMap.put(mediaId, book);
                }
                int categoryId = resultSet.getInt("categoryId");
                String categoryName = resultSet.getString("categoryName");

                if(categoryId != 0 && categoryName != null) {
                    Category category = new Category(categoryId, categoryName);
                    book.addCategory(category);
                }

            }
            books.addAll(bookMap.values());
        }
        return books;
    }

    public List<Book> searchByAuthor(String userInput) throws SQLException {
        List<Book> results = new ArrayList<>();
        String sql = "SELECT * FROM Media WHERE LOWER(author) LIKE ? AND mediaType = 'Book'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + userInput.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = buildBook(rs);

                List<Category> categories = categoryDao.getAllCategoriesByMediaId(book.getMediaId());
                if (categories != null) {
                    book.setCategories(categories);
                }

                results.add(book);
            }

        }

        return results;
    }
    public List<Book> searchByBookName(String userInput) throws SQLException {
        List<Book> results = new ArrayList<>();
        String sql ="SELECT * FROM Media WHERE LOWER(mediaName) LIKE ? AND mediaType = 'BOOK'";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + userInput.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book book = buildBook(rs);
                results.add(book);

            }
        }
        return results;
    }


    public List<Book> searchByISBN(String isbnInput) throws SQLException {
        List<Book> results = new ArrayList<>();
        String sql = "SELECT * FROM Media WHERE LOWER(ISBN) LIKE ? AND mediaType = 'Book'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + isbnInput.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = buildBook(rs);

                    List<Category> categories = categoryDao.getAllCategoriesByMediaId(book.getMediaId());
                    if (categories != null) {
                        book.setCategories(categories);
                    }

                    results.add(book);
                }
            }
        }

        return results;
    }

     public void addBook(Book book) throws SQLException {
         String sql = "INSERT INTO Media (mediaName, mediaType, author, isbn, pageCount, partOfCourse) VALUES (?, ?, ?, ?, ?, ?)";
         try(PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, book.getMediaName());
                stmt.setString(2, book.getMediaType().toString());
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getIsbn());
                stmt.setInt(5, book.getPageCount());
                stmt.setBoolean(6, book.getPartOfCourse());

                stmt.executeUpdate();
         }
         catch(SQLException e) {
             e.printStackTrace();
         }
    }
}