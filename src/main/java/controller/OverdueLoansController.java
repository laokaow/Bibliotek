package controller;

import dao.DAOFactory;
import dao.LoanDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.Loan;
import model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OverdueLoansController implements SceneManager.ControlledScene {

    private User currentUser;
    private LoanDAO loanDAO;
    private List<Loan> loans = new ArrayList<>();


    @FXML
    private ListView<String> loansListView;

    @Override
    public void setData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            loanDAO = DAOFactory.getLoanDAO();
            loadOverdueLoans();
        }
    }
    @FXML
    private void returnSelectedLoan() {
        int selectedIndex = loansListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            Loan selectedLoan = loans.get(selectedIndex); // Vi använder Loan direkt

            try {
                Loan loanFromDb = loanDAO.getLoanById(selectedLoan.getLoanId());

                System.out.println("Updating loanId " + loanFromDb.getLoanId() + " with returnDate " + LocalDate.now());

                loanDAO.returnLoan(loanFromDb, LocalDate.now());

                loadOverdueLoans();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        } else {
            System.out.println("Inga lån valda");
        }
    }
    private void loadOverdueLoans() {
        try {
            loans = loanDAO.getOverdueLoansForUser(currentUser.getUserId()); // Uppdatera listan

            loansListView.getItems().clear();
            for (Loan loan : loans) {
                String mediaTitle = loan.getCopy().getMediaName();
                String dueDateStr = loan.getDueDate() != null ? loan.getDueDate().toString() : "Ingen förfallodag";

                String info = String.format("Titel: %s | Förfallo: %s", mediaTitle, dueDateStr);
                loansListView.getItems().add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.showScene("CustomerView.fxml", currentUser);
    }


}