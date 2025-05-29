package controller;

import dao.DAOFactory;
import dao.LoanDAO;
import dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Loan;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class AllUsersController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> firstNameCol;
    @FXML private TableColumn<User, String> lastNameCol;
    @FXML private TableColumn<User, String> emailCol;
    @FXML private TableColumn<User, String> phoneCol;
    @FXML private TableColumn<User, String> userTypeCol;
    @FXML private TableColumn<User, String> loanCountCol;

    private final UserDAO userDAO = new DAOFactory().getUserDAO();
    private final LoanDAO loanDAO;

    public AllUsersController() {
        loanDAO = new DAOFactory().getLoanDAO();
    }

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));

        loanCountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    User user = (User) getTableRow().getItem();
                    try {
                         List<Loan> count = loanDAO.getActiveLoansForUser(user.getUserId());
                        setText(String.valueOf(count));
                    } catch (SQLException e) {
                        setText("Fel");
                        e.printStackTrace();
                    }
                }
            }
        });

        ObservableList<User> users = FXCollections.observableArrayList(userDAO.getAllUsers());
        userTable.setItems(users);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userTable.getScene().getWindow();
        stage.close();
    }
}