package controller;

import dao.DAOFactory;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;

public class RegisterController implements SceneManager.ControlledScene {

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField txtPinCode;
    @FXML private PasswordField txtPinCodeConfirm;
    @FXML private ComboBox<User.UserType> cmbUserType;
    @FXML private Label lblStatus;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = DAOFactory.getUserDAO();
        System.out.println("RegisterController initierad.");
        this.userDAO = DAOFactory.getUserDAO();

        if (this.userDAO == null) {
            System.err.println("DAOFactory.getUserDAO() returnerade null!");
        }
        cmbUserType.getItems().setAll(User.UserType.values());
    }

    @FXML
    private void handleRegister() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pinCode = txtPinCode.getText();
        String pinConfirm = txtPinCodeConfirm.getText();
        User.UserType userType = cmbUserType.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || pinCode.isEmpty() || pinConfirm.isEmpty() || userType == null) {
            showStatus("Fyll i alla obligatoriska fält.");
            return;
        }

        if (!pinCode.equals(pinConfirm)) {
            showStatus("PIN-koder matchar inte.");
            return;
        }

        User newUser = new User(0, firstName, lastName, email, phone, pinCode, userType);
        User created = userDAO.createUser(newUser);

        if (created != null) {
            showStatus("Registrering lyckades! Logga in.");
            SceneManager.showLoginView();
        } else {
            showStatus("Något gick fel vid registrering.");
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.showLoginView();
    }

    private void showStatus(String message) {
        lblStatus.setText(message);
        lblStatus.setVisible(true);
    }

    @Override
    public void setData(Object data) {
        // Ingen data behövs
    }
}