package controller;

import dao.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Category;
import model.Dvd;
import model.Media;
import model.Actor;
import util.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddDvdController {

    @FXML private TextField mediaNameField;
    @FXML private TextField durationField;
    @FXML private CheckBox partOfCourseCheckBox;
    @FXML private VBox categoryCheckboxContainer;
    @FXML private TextField actorNameField;
    @FXML private ListView<String> actorListView;
    @FXML private TextField ageLimitField;
    @FXML private TextField productionCountryField;
    @FXML private TextField directorField;

    private final List<String> actorNames = new ArrayList<>();
    private final List<CheckBox> categoryCheckboxes = new ArrayList<>();

    //Skapar instanser av DAO-klasserna genom DAOFactory
    private final CategoryDAO categoryDAO = DAOFactory.getCategoryDAO();
    private final MediaCategoryDAO mediaCategoryDAO = DAOFactory.getMediaCategoryDAO();
    private final ActorDAO actorDAO = DAOFactory.getActorDAO();
    private final MediaActorDAO mediaActorDAO = DAOFactory.getMediaActorDAO();
    private final DvdDAO dvdDAO = DAOFactory.getDvdDAO();

    @FXML
    public void initialize() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();

            for (Category category : categories) {
                CheckBox checkBox = new CheckBox(category.getCategoryName());
                checkBox.setUserData(category);
                categoryCheckboxContainer.getChildren().add(checkBox);
                categoryCheckboxes.add(checkBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Fel vid initiering", "Kunde inte hämta kategorier från databasen.");
        }
    }

    @FXML
    public void handleAddActor() {
        String name = actorNameField.getText().trim();
        if (!name.isEmpty()) {
            actorNames.add(name);
            actorListView.getItems().add(name);
            actorNameField.clear();
        }
    }

    @FXML
    public void handleAddDvd() {
        try {
            String mediaName = mediaNameField.getText().trim();
            int duration = Integer.parseInt(durationField.getText().trim());
            int ageLimit = Integer.parseInt(ageLimitField.getText().trim());
            String productionCountry = productionCountryField.getText().trim();
            String director = directorField.getText().trim();
            boolean partOfCourse = partOfCourseCheckBox.isSelected();

            Dvd dvd = new Dvd(
                    0,
                    mediaName,
                    Media.MediaType.DVD,
                    partOfCourse,
                    ageLimit,
                    productionCountry,
                    director,
                    duration
            );

            int newMediaId = addDvdAndReturnMediaId(dvd);

            List<Category> selectedCategories = categoryCheckboxes.stream()
                    .filter(CheckBox::isSelected)
                    .map(cb -> (Category) cb.getUserData())
                    .collect(Collectors.toList());

            for (Category category : selectedCategories) {
                mediaCategoryDAO.addCategoryToMedia(newMediaId, category.getCategoryId());
            }


            for (String fullName : actorNames) {
                String[] parts = fullName.trim().split(" ", 2);
                String firstName = parts.length > 0 ? parts[0] : "";
                String lastName = parts.length > 1 ? parts[1] : "";

                Actor actor = actorDAO.findByName(firstName, lastName);
                if (actor == null) {
                    int actorId = actorDAO.addActor(new Actor(0, firstName, lastName));
                    actor = new Actor(actorId, firstName, lastName);
                }

                mediaActorDAO.addMediaActor(newMediaId, actor.getActorId());
            }

            showAlert("Klar", "DVD tillagd med ID: " + newMediaId);
            clearForm();

        } catch (NumberFormatException e) {
            showAlert("Felaktigt inmatning", "Duration och åldersgräns måste vara siffror.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Databasfel", e.getMessage());
        }
    }

    private int addDvdAndReturnMediaId(Dvd dvd) throws SQLException {
        String sql = "INSERT INTO Media (mediaName, mediaType, duration, partOfCourse) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             var stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, dvd.getMediaName());
            stmt.setString(2, dvd.getMediaType().toString());
            stmt.setInt(3, dvd.getDuration());
            stmt.setBoolean(4, dvd.getPartOfCourse());

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

    private void clearForm() {
        mediaNameField.clear();
        durationField.clear();
        partOfCourseCheckBox.setSelected(false);
        actorNameField.clear();
        actorListView.getItems().clear();
        actorNames.clear();
        categoryCheckboxes.forEach(cb -> cb.setSelected(false));
        ageLimitField.clear();
        productionCountryField.clear();
        directorField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Meddelande");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}