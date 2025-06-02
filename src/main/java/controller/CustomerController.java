package controller;

import javafx.fxml.FXML;
import model.User;
import util.SessionManager;

public class CustomerController implements SceneManager.ControlledScene {
    private User currentUser;
    public void setCurrentUser(User user){
        this.currentUser = user;
    }
    @FXML
    private javafx.scene.control.Label welcomeLabel;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            if (welcomeLabel != null) {
                welcomeLabel.setText("Hej, " + currentUser.getName() + "!");
            }
        }
    }
    @FXML
    private void handleOverDueLoans() {
        if(currentUser != null) {
            SceneManager.showScene("OverDueLoansView.fxml", currentUser);
        }
    }

    @FXML
    private void handleViewLoans() {
        if (currentUser != null) {
            SceneManager.showScene("LoanOverView.fxml", currentUser);
        }
    }

    @FXML
    private void handleSearchMedia() {
        SceneManager.showScene("Main.fxml", currentUser);
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        SceneManager.showMainView();
    }
}