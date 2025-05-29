package controller;

import dao.DAOFactory;
import dao.LoanDAO;
import dto.LoanDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.Loan;
import model.User;
import util.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class LoanOverviewController implements SceneManager.ControlledScene {

    @FXML private ListView<String> loansListView;

    private User currentUser;
    private LoanDAO loanDAO;

    @Override
    public void setData(Object data) throws SQLException {
        if (data instanceof User) {
            this.currentUser = (User) data;
            loanDAO = DAOFactory.getLoanDAO();
            loadLoans();
        }
    }

    private void loadLoans() {
        User user = SessionManager.getLoggedInUser();
        if (user == null) return;

        List<LoanDTO> loans = DAOFactory.getLoanDAO().getLoanDTOsByUserId(user.getUserId());
        loansListView.getItems().clear();
        for (LoanDTO loanDTO : loans) {
            String info = String.format("Titel: %s | Förfallo: %s",
                    loanDTO.getMediaName(),
                    loanDTO.getDueDate().toString());
            loansListView.getItems().add(info);
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.showScene("CustomerView.fxml", currentUser);
    }
}