package controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import util.SessionManager;

public class StaffController {

    //Modalmetod för att öppna popups
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

    //Nedanstående namn är inkonsekventa. Form passar bättre då det är en modal som öppnas
    //Kommer ej ändra det då det ändå funkar
    //Vid påbyggnad av kod bör det ändras för klarhet
    @FXML
    private void handleAddMedia(){
        openModal("/view/SelectMediaTypeView.fxml", "Välj Media att lägga till");
    }
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
    @FXML
    private void handleManageMedia() {
        openModal("/view/ManageMediaView.fxml", "Hantera Media");
    }

    @FXML
    private void handleGetUsers() {
        openModal("/view/AllUsersView.fxml", "Alla användare");
    }

    @FXML
    private void handleLogout() {
            SessionManager.clear();
            SceneManager.showMainView();
        }
    }