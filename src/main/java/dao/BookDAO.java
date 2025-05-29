package dao;

import model.Book;
import model.Category;
import model.Media;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;
     private CategoryDAO categoryDao;
     List<Book> bookList = new ArrayList<>();

     public BookDAO(Connection connection, CategoryDAO categoryDao) {
         this.connection = connection;
         this.categoryDao = categoryDao;
     }

     public void loadBooks() throws SQLException {
         bookList = getAllBooks();
     }

     public List<Book> getAllBooks() throws SQLException {
        String sql = "select * from Media where mediaType = 'Book'";
         List<Book> books = new ArrayList<>();
         Connection con = null;
         PreparedStatement preparedStatement = null;
         ResultSet resultSet = null;


         try {
             con = DatabaseConnection.getConnection();
             preparedStatement = con.prepareStatement(sql);
             resultSet = preparedStatement.executeQuery();
             Book currentBook = null;
             while (resultSet.next()) {

                 int mediaId = resultSet.getInt("mediaId");
                 String mediaName = resultSet.getString("mediaName");
                 Media.MediaType mediaType = Media.MediaType.valueOf(resultSet.getString("mediaType").toUpperCase());
                 boolean partOfCourse = resultSet.getBoolean("partOfCourse");
                 String author = resultSet.getString("author");
                 String isbn = resultSet.getString("ISBN");
                 int pageCount = resultSet.getInt("pageCount");



                     currentBook = new Book(
                             mediaId,
                             mediaName,
                             mediaType,
                             partOfCourse,
                             author,
                             isbn,
                             pageCount,
                             new ArrayList<>()
                     );
                     // koden nedanför hanterar böckers kategorier och ser till att .getAllCategoriesByMediaId() inte retunerar null
                   List <Category> categories = categoryDao.getAllCategoriesByMediaId(mediaId);
                   if(categories != null) {
                       currentBook.setCategories(categories);
                   }
                    books.add(currentBook);
                 }

                 //currentBook.getCategories().add(category);
             }

         catch (SQLException e) {
             e.printStackTrace();
             System.out.println("SQL error");
         } finally {
             if (resultSet != null) resultSet.close();
             if (preparedStatement != null) preparedStatement.close();
             if (con != null) con.close();
         }
         return books;

     }

     public List<Book> searchByAuthor (String userInput) throws SQLException {
         List<Book> results = new ArrayList<>();
         for (Book book : bookList) {
                 if (book.getAuthor() != null && book.getAuthor().equals(userInput)) {
                     results.add(book);
             }
         }
         return results;
     }


     public void addBook(Book book) throws SQLException {
         String sql = "INSERT INTO Media (mediaName, mediaType, author, isbn, pageCount, partOfCourse) VALUES (?, ?, ?, ?, ?, ?)";
         try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql)) {
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
//     Book book = new Book(
//                        resultSet.getInt("mediaId"),
//                        resultSet.getString("name"),
//                        Media.MediaType.valueOf(resultSet.getString("mediaType").toUpperCase()),
//                        resultSet.getBoolean("partOfCourse"),
//                        resultSet.getString("author"),
//                        resultSet.getString("ISBN"),
//                        resultSet.getInt("pageCount"),
//
//
//
//                );


// String sql = "SELECT\n" +
//                 "    m.mediaId,\n" +
//                 "    m.mediaName,\n" +
//                 "    m.mediaType,\n" +
//                 "    m.author,\n" +
//                 "    m.isbn,\n" +
//                 "    m.pageCount,\n" +
//                 "    m.partOfCourse,\n" +
//                 "    c.categoryId,\n" +
//                 "    c.categoryName\n" +
//                 "FROM Media m\n" +
//                 "JOIN MediaCategory mc ON m.mediaId = mc.mediaId\n" +
//                 "JOIN Category c ON mc.category = c.categoryId\n" +
//                 "WHERE mediaType = 'BOOK';\n";