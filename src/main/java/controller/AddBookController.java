package controller;

import dao.BookDAO;
import dao.CategoryDAO;
import dao.MediaCategoryDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Book;
import model.Category;
import model.Media;
import util.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddBookController implements Initializable {

    @FXML private TextField mediaNameField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField pageCountField;
    @FXML private CheckBox partOfCourseCheckBox;
    @FXML private VBox categoryCheckboxContainer;

    private MediaCategoryDAO mediaCategoryDAO;

    private final List<CheckBox> categoryCheckboxes = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            CategoryDAO categoryDAO = new CategoryDAO(conn);
            mediaCategoryDAO = new MediaCategoryDAO(conn);
            BookDAO bookDAO = new BookDAO(conn, categoryDAO);

            List<Category> categories = categoryDAO.getAllCategories();
            for (Category category : categories) {
                CheckBox checkbox = new CheckBox(category.getCategoryName());
                checkbox.setUserData(category); // Kopplar objektet till checkboxen
                categoryCheckboxContainer.getChildren().add(checkbox);
                categoryCheckboxes.add(checkbox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Fel vid laddning av kategorier", e.getMessage());
        }
    }

    @FXML
    public void handleAddBook() {
        try {
            String title = mediaNameField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            int pageCount = Integer.parseInt(pageCountField.getText().trim());
            boolean partOfCourse = partOfCourseCheckBox.isSelected();

            //Samla valda kategorier
            List<Category> selectedCategories = categoryCheckboxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(cb -> (Category) cb.getUserData())
                    .collect(Collectors.toList());

            Book book = new Book(
                    0, // mediaId sätts av databasen
                    title,
                    Media.MediaType.BOOK,
                    partOfCourse,
                    author,
                    isbn,
                    pageCount,
                    selectedCategories
            );
            //Lägga till bok och få tillbaka det genererade mediaId
            int newMediaId = addBookAndReturnMediaId(book);

            for (Category category : selectedCategories) {
                mediaCategoryDAO.addCategoryToMedia(newMediaId, category.getCategoryId());
            }

            showAlert("Bok tillagd", "Boken '" + title + "' har lagts till med ID " + newMediaId);
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Felaktigt värde", "Skriv in ett korrekt heltal i 'Antal sidor'.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Fel vid tillägg", e.getMessage());
        }
    }

    private int addBookAndReturnMediaId(Book book) throws SQLException {
        String sql = "INSERT INTO Media (mediaName, mediaType, author, isbn, pageCount, partOfCourse) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             var stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getMediaName());
            stmt.setString(2, book.getMediaType().toString());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getIsbn());
            stmt.setInt(5, book.getPageCount());
            stmt.setBoolean(6, book.getPartOfCourse());

            stmt.executeUpdate();

            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Misslyckades att hämta genererat mediaId.");
                }
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Meddelande");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        mediaNameField.clear();
        authorField.clear();
        isbnField.clear();
        pageCountField.clear();
        partOfCourseCheckBox.setSelected(false);
        for (CheckBox cb : categoryCheckboxes) {
            cb.setSelected(false);
        }
    }
}