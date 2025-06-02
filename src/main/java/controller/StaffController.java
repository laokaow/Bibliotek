package controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.User;
import util.SessionManager;

import java.sql.SQLException;

public class StaffController implements SceneManager.ControlledScene {
    private User currentUser;
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    @FXML
    private javafx.scene.control.Label welcomeLabel;
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
    private void handleSearchMedia() {
        SceneManager.showScene("Main.fxml", currentUser);
    }

    @FXML
    private void handleGetUsers() {
        openModal("/view/AllUsersView.fxml", "Alla användare");
    }

    @FXML
    private void handleLogout() {
            SessionManager.logout();
            SceneManager.showMainView();
        }


    @Override
    public void setData(Object data) {
        if (data instanceof User user) {
            this.currentUser = user;
            if (welcomeLabel != null) {
                welcomeLabel.setText("Hej, " + user.getName() + "!");
            }
        }
    }
}