package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import dao.UserDAO;
import model.User;
import dao.DAOFactory;

public class ProfileController implements SceneManager.ControlledScene {

    @FXML
    private Label nameLabel;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    private User currentUser;
    private UserDAO userDAO;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            this.userDAO = DAOFactory.getUserDAO();
            loadUserData();
        } else {
            showError("Felaktig användardata mottagen.");
        }
    }

    private void loadUserData() {
        nameLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhoneNumber());
    }

    @FXML
    private void handleSaveChanges() {
        if (currentUser == null) {
            showError("Ingen användare är inloggad.");
            return;
        }

        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();

        boolean updatedEmail = false;
        boolean updatedPhone = false;

        try {
            if (!newEmail.equals(currentUser.getEmail())) {
                updatedEmail = userDAO.updateEmail(newEmail, currentUser.getUserId());
            }
            if (!newPhone.equals(currentUser.getPhoneNumber())) {
                updatedPhone = userDAO.updatePhoneNumber(newPhone, currentUser.getUserId());
            }

            if (updatedEmail || updatedPhone) {
                currentUser.setEmail(newEmail);
                currentUser.setPhoneNumber(newPhone);
                showInfo("Uppdatering lyckades.");
            } else {
                showInfo("Inga ändringar gjordes.");
            }
        } catch (Exception e) {
            showError("Kunde inte uppdatera användaren: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}