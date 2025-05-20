package controller;

import dao.DAOFactory;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import model.User;

import java.awt.event.ActionEvent;

public class LoginController implements SceneManager.ControlledScene {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPinCode;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkToRegister;
    @FXML private Label lblStatus;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = DAOFactory.getUserDAO();

        btnLogin.setOnAction(event -> handleLogin());
        linkToRegister.setOnAction(event -> SceneManager.showRegisterView());
    }

    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String pin = txtPinCode.getText();

        if(email.isEmpty() || pin.isEmpty()) {
            lblStatus.setText("Fyll i alla fält.");
            return;
        }

        User user = userDAO.authenticateUser(email, pin);

        if(user != null) {
            lblStatus.setText("Inloggning lyckades!");
            SceneManager.showProfileView(user);
        } else {
            lblStatus.setText("Fel e-post eller PIN-kod.");
        }
    }

    @Override
    public void setData(Object data) {
        // Ingen data behövs här
    }
}