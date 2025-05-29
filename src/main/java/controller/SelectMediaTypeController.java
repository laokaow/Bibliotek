package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectMediaTypeController {

    @FXML
    private void handleAddBook() {
        openModal("/view/AddBookView.fxml", "Lägg till Bok");
    }

    @FXML
    private void handleAddJournal() {
        openModal("/view/AddJournalView.fxml", "Lägg till Tidskrift");
    }

    @FXML
    private void handleAddDvd() {
        openModal("/view/AddDvdView.fxml", "Lägg till DVD");
    }

    private void openModal(String resourcePath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}