package controller;

import javafx.fxml.FXML;
import model.User;
import util.SessionManager;

public class CustomerController implements SceneManager.ControlledScene {

    private User currentUser;
    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
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
        SceneManager.showScene("SearchView.fxml", currentUser);
    }

    @FXML
    private void handleLogout() {
        SessionManager.clear();
        SceneManager.showLoginView();
    }
}