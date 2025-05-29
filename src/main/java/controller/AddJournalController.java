package controller;

import dao.JournalDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Journal;
import model.Media.MediaType;
import util.DatabaseConnection;

import java.sql.SQLException;

public class AddJournalController {

    @FXML
    private TextField mediaNameField;

    @FXML
    private TextField issueNumberField;

    @FXML
    private CheckBox partOfCourseCheckBox;

    @FXML
    private Button saveButton;

    private JournalDAO journalDAO;

    @FXML
    private void initialize() {
        try {
            journalDAO = new JournalDAO(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Databasfel", "Kunde inte ansluta till databasen.");
        }

        saveButton.setOnAction(event -> handleSave());
    }

    @FXML
    private void handleSave() {
        String mediaName = mediaNameField.getText().trim();
        String issueNumber = issueNumberField.getText().trim();
        boolean partOfCourse = partOfCourseCheckBox.isSelected();

        if (mediaName.isEmpty() || issueNumber.isEmpty()) {
            showAlert("Felaktig inmatning", "Alla fält måste fyllas i.");
            return;
        }

        try {
            Journal journal = new Journal(0, mediaName, MediaType.JOURNAL, partOfCourse, issueNumber);
            journalDAO.addJournal(journal);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Fel", "Det gick inte att spara tidskriften.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}