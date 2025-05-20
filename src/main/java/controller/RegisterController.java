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
    @FXML private ComboBox<User.UserType> cmbUserType;  // Enum med usertyper
    @FXML private Button btnRegister;
    @FXML private Label lblStatus;
    @FXML private Hyperlink linkToLogin;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = DAOFactory.getUserDAO();

        cmbUserType.getItems().setAll(User.UserType.values()); // Visa alla typer
        cmbUserType.setPromptText("Användartyp");

        btnRegister.setOnAction(event -> handleRegister());

        linkToLogin.setOnAction(e -> SceneManager.showLoginView());
    }

    private void handleRegister() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pinCode = txtPinCode.getText();
        String pinConfirm = txtPinCodeConfirm.getText();
        User.UserType userType = cmbUserType.getValue();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || pinCode.isEmpty() || userType == null) {
            lblStatus.setText("Fyll i alla obligatoriska fält.");
            return;
        }

        if(!pinCode.equals(pinConfirm)) {
            lblStatus.setText("PIN-koder matchar inte.");
            return;
        }

        User newUser = new User(0, firstName, lastName, email, phone, pinCode, userType);

        User created = userDAO.createUser(newUser);

        if(created != null) {
            lblStatus.setText("Registrering lyckades! Logga in.");
            SceneManager.showLoginView();
        } else {
            lblStatus.setText("Något gick fel vid registrering.");
        }
    }

    @Override
    public void setData(Object data) {
        // Ingen data behövs
    }

}