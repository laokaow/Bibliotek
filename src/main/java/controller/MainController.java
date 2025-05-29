package controller;

import javafx.fxml.FXML;
 public class MainController {

        @FXML
        private void handleLogin() {
            SceneManager.showLoginView();
        }

        @FXML
        private void handleRegister() {
            SceneManager.showRegisterView();
        }
    }