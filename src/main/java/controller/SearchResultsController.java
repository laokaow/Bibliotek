package controller;

import dao.CopyDAO;
import dao.LoanDAO;
import dao.ReceiptDAO;
import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import model.*;
import javafx.scene.control.Label;
import model.Copy;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsController {

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblId;
    @FXML
    private Label lblAuthorOrDirector;
    @FXML
    private FlowPane categoryTags;
    @FXML
    private Button btnLoan;
    @FXML
    private Label lblDuration;

    private Media media;
    private User currentUser;
    private LoanDAO loanDAO;
    private CopyDAO copyDAO;
    private ReceiptDAO receiptDAO;
    private UserDAO userDAO;

    public void setData(Media media, User currentUser, LoanDAO loanDAO, CopyDAO copyDAO, ReceiptDAO receiptDAO, UserDAO userDAO) {
        this.media = media;
        this.currentUser = currentUser;
        this.loanDAO = loanDAO;
        this.copyDAO = copyDAO;
        this.receiptDAO = receiptDAO;
        this.userDAO = userDAO;
        lblTitle.setText(media.getMediaName());
        lblId.setText("(ID: " + media.getMediaId() + ")");

        if (media instanceof Book book) {
            lblAuthorOrDirector.setText("Författare: " + book.getAuthor());
        } else if (media instanceof Dvd dvd) {
            lblAuthorOrDirector.setText("Regissör: " + dvd.getDirector());
            lblDuration.setText("Längd: " + dvd.getDuration() + " min");

        } else {
            lblAuthorOrDirector.setText("");
        }

        categoryTags.getChildren().clear();
        if (media instanceof Book book) {
            for (Category category : book.getCategory()) {
                Label tag = new Label(category.getCategoryName());
                tag.setStyle("-fx-background-color: #E1F5FE; -fx-padding: 5 10; -fx-background-radius: 10; -fx-text-fill: #0277BD;");
                categoryTags.getChildren().add(tag);
            }
        } else if (media instanceof Dvd dvd) {
            for (Category category : dvd.getCategory()) {
                Label tag = new Label(category.getCategoryName());
                tag.setStyle("-fx-background-color: #E1F5FE; -fx-padding: 5 10; -fx-background-radius: 10; -fx-text-fill: #0277BD;");
                categoryTags.getChildren().add(tag);
            }
        }


        btnLoan.setOnAction(e -> handleLoan());
    }

    private void handleLoan() {
        if (currentUser == null) {
            showAlert("Ej inloggad", "Du måste logga in för att låna media.");
            return;
        }

        try {
            int currentLoans = loanDAO.getUserLoanCount(currentUser.getUserId());
            int maxLoans = userDAO.getMaxLoanCount(currentUser.getUserType().toString());

            if (currentLoans >= maxLoans) {
                showAlert("Lånegräns nådd", "Du har nått din maximala lånegräns.");
                return;
            }

            List<Copy> availableCopies = copyDAO.getCopiesByMediaId2(media.getMediaId())
                    .stream()
                    .filter(copy -> copy.getAvailability() == Copy.AvailabilityStatus.AVAILABLE)
                    .filter(copy -> !copy.isReferenceCopy())
                    .toList();

            if (availableCopies.isEmpty()) {
                showAlert("Ej tillgänglig", "Inga exemplar är tillgängliga för utlåning.");
                return;
            }

            Copy copyToLoan = availableCopies.get(0);
            Loan loan = loanDAO.createLoan(currentUser, copyToLoan, LocalDate.now());

            // Skapa kvittoinfo som text
            String receiptInfo = "Media: " + media.getMediaName() + "\n"
                    + "Exemplar-ID: " + copyToLoan.getCopyId() + "\n"
                    + "Lånedatum: " + loan.getBorrowDate() + "\n"
                    + "Förfallodatum: " + loan.getDueDate() + "\n"
                    + "Låntagare: " + currentUser.getFirstName() + " " + currentUser.getLastName();

            // Lägger in kvittot i databasen
            int receiptId = receiptDAO.createReceipt(currentUser, receiptInfo, LocalDateTime.now());

            // Visar kvittot vid lån
            String fullReceipt = "Kvitto-ID: " + receiptId + "\n" + receiptInfo;
            showAlert("Lån registrerat", fullReceipt);

        } catch (SQLException e) {
            showAlert("Fel", "Ett fel uppstod vid utlåning.");
            e.printStackTrace();
        } catch (IllegalStateException ise) {
            showAlert("Ej tillåtet", ise.getMessage());
            ise.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}