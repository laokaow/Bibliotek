package controller;

import dao.DAOFactory;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import service.LoginService;

public class LoginController implements SceneManager.ControlledScene {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkToRegister;
    @FXML private Label errorLabel;
    @FXML private Label lblStatus;

    private LoginService loginService;

    @FXML
    private void initialize() {
        UserDAO userDAO = DAOFactory.getUserDAO();
        loginService = new LoginService(userDAO);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String pin = passwordField.getText().trim();

        if (email.isEmpty() || pin.isEmpty()) {
            showStatus("Fyll i alla fält.");
            return;
        }

        User user = loginService.login(email, pin);
        if (user != null) {
            System.out.println("Inloggad som: " + user.getUserType());
            // TODO: Byt scen här med SceneManager.showXView()
        } else {
            showError("Fel e-post eller PIN-kod.");
        }
    }

    @FXML
    private void goToRegister() {
        SceneManager.showRegisterView();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        lblStatus.setVisible(false);
    }

    private void showStatus(String message) {
        lblStatus.setText(message);
        lblStatus.setVisible(true);
        errorLabel.setVisible(false);
    }

    @Override
    public void setData(Object data) {
        // Ingen data behövs här
    }
}