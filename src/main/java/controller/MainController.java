package controller;
import dao.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.*;
import util.DatabaseConnection;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainController implements SceneManager.ControlledScene {
    private BookDAO bookDAO;
    private DvdDAO dvdDAO;
    private JournalDAO journalDAO;
    private CategoryDAO categoryDAO;
    private ActorDAO actorDAO;
    private CopyDAO copyDAO;
    private MediaDAO mediaDAO;
    private LoanDAO loanDAO;
    private ReceiptDAO receiptDAO;
    private UserDAO userDAO;
    private User currentUser = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private DAOFactory daoFactory;
    @FXML
    private Label lblHeader;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnServices;
    @FXML
    private Button btnInspiration;
    @FXML
    private Button btnMedia;
    @FXML
    private Button btnLiterature;
    @FXML
    private Button btnHelp;
    @FXML
    private Button btnAccount;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbSearchCategory;
    @FXML
    private VBox searchResultsBox;
    @FXML
    private Button btnSearch;
    @FXML
    private Label lblDiscover;
    @FXML
    private ImageView imgBookShelf;
    @FXML
    private Button btnSeeMoreBooks;

    @FXML
    private ImageView imgEvents;
    @FXML
    private Button btnSeeMoreEvents;

    @FXML
    private ImageView imgMedia;
    @FXML
    private Button btnSeeMoreMedia;

    @FXML
    private Label lblFooter;
    @FXML
    private VBox vboxResults;

    @FXML private Button loginButton;

    @FXML private Button registerButton;

    @FXML
    private void initialize() {
        updateAuthButtons();
        try {
            Connection connection = DatabaseConnection.getConnection();
            DAOFactory.init(connection);
            this.bookDAO = DAOFactory.getBookDAO();
            this.dvdDAO = DAOFactory.getDvdDAO();
            this.journalDAO = DAOFactory.getJournalDAO();
            this.categoryDAO = DAOFactory.getCategoryDAO();
            this.actorDAO = DAOFactory.getActorDAO();
            this.copyDAO = DAOFactory.getCopyDAO();
            this.mediaDAO = DAOFactory.getMediaDAO();
            this.loanDAO = DAOFactory.getLoanDAO();
            this.receiptDAO = DAOFactory.getReceiptDAO();
            this.userDAO = DAOFactory.getUserDAO();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //Skapar drop-down listan för filtrering/sökning
        System.out.println("LibraryMainController initialized.");
        cmbSearchCategory.getItems().addAll(
                "Böcker",
                "Författare",
                "ISBN",
                "Artiklar",
                "Media",
                "Dvd",
                "Längd",
                "Kategori",
                "Skådespelare",
                "CopyStatus",
                "Regissör"
        );
    }
    public User getCurrentUser() {
        return this.currentUser;
    }
    public void updateAuthButtons() {
        boolean loggedIn = util.SessionManager.isLoggedIn();
        loginButton.setVisible(!loggedIn);
        registerButton.setVisible(!loggedIn);
    }

    private void displayMediaResultsWithLoanButton(List<? extends Media> mediaList) {
        searchResultsBox.getChildren().clear();

        for (Media media : mediaList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchView.fxml"));
                Parent searchResultItem = loader.load();

                SearchResultsController controller = loader.getController();
                controller.setData(media, currentUser, loanDAO, copyDAO, receiptDAO, userDAO);

                searchResultsBox.getChildren().add(searchResultItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSearch() {
        searchResultsBox.getChildren().clear(); // Rensa tidigare resultat
        String query = txtSearch.getText().trim();
        String selected = cmbSearchCategory.getValue();
        System.out.println("Söktext: '" + query + "', kategori: '" + selected + "'");

        if (selected == null || selected.isBlank()) {
            Label lbl = new Label("Välj en sökkategori.");
            searchResultsBox.getChildren().add(lbl);
            return;
        }

        try {
            switch (selected) {
                case "Böcker" -> {
                    System.out.println("Söker böcker med query: '" + query + "'");
                    List<Book> books;
                    if (query.isBlank()) {
                        books = bookDAO.getAllBooks();
                        displayMediaResultsWithLoanButton(books);
                    } else {
                        books = bookDAO.searchByBookName(query);
                        displayMediaResultsWithLoanButton(books);
                    }

                }
                case "Författare" -> {
                    List<Book> searchByAuthor = bookDAO.getAllBooks();
                    if (query.isBlank()) {
                        Label lbl = new Label("Skriv in författarnamn.");
                        searchResultsBox.getChildren().add(lbl);
                    }
                    else {
                        searchByAuthor = bookDAO.searchByAuthor(query);
                        displayMediaResultsWithLoanButton(searchByAuthor);
                    }


                }
                case "ISBN" -> {
                    if (query.isBlank()) {
                        Label lbl = new Label("Skriv in ISBN.");
                        searchResultsBox.getChildren().add(lbl);
                    }
                    List<Book> filteredBooks = bookDAO.searchByISBN(query);
                    displayMediaResultsWithLoanButton(filteredBooks);
                }
                case "Artiklar" -> {
                    List<Journal> journals = journalDAO.getAllJournals();
                    displayMediaResultsWithLoanButton(journals);
                }
                case "Media" -> {
                    if(query.isBlank()) {
                        List<Media> mediaList = mediaDAO.getMedia();
                        displayMediaResultsWithLoanButton(mediaList);
                    }
                    else {
                        List<Media> mediaList = mediaDAO.searchMedia(query);
                    }

                }
                case "Dvd" -> {
                    List<Dvd> dvds = dvdDAO.getAllDvd();
                    displayMediaResultsWithLoanButton(dvds);
                }
                case "Längd" -> {
                    if (query.isBlank()) {
                        Label lbl = new Label("Skriv in en längd i minuter.");
                        searchResultsBox.getChildren().add(lbl);
                        return;
                    }
                    int duration = Integer.parseInt(query);
                    List<Dvd> filteredDvds = dvdDAO.searchDvdByDuration(duration);
                    displayMediaResultsWithLoanButton(filteredDvds);
                }
                case "Kategori" -> {
                    List<Media> mediaByCategory = categoryDAO.getMediaByCategoryName(query);
                    displayMediaResultsWithLoanButton(mediaByCategory);
                }
                case "Skådespelare" -> {
                    List<Dvd> actorsByDvd = dvdDAO.searchDvdByActorName(query);
                    displayMediaResultsWithLoanButton(actorsByDvd);

                }
                case "CopyStatus" -> {
                    if (query.isBlank()) {
                        Label lbl = new Label("Skriv in en media titel.");
                        searchResultsBox.getChildren().add(lbl);
                        return;
                    }
                    List<Copy> copies = copyDAO.searchCopiesByMediaName(query);
                    copies.forEach(copy -> {
                        Label lbl = new Label("Exemplar ID: " + copy.getCopyId() + " MediaName: " + copy.getMediaName() + ", Status: " + copy.getAvailability());
                        searchResultsBox.getChildren().add(lbl);
                    });
                }
                case "Regissör" -> {
                    List<Dvd> dvdByDirector = dvdDAO.searchByDirector(query);
                    if (query.isBlank()) {
                        Label lbl = new Label("Skriv in en regissörs namn");
                        searchResultsBox.getChildren().add(lbl);
                    }
                    if(!query.isBlank()) {
                        displayMediaResultsWithLoanButton(dvdByDirector);
                    }
                    if(dvdByDirector.isEmpty()) {
                        System.out.println("Inga resultat för: " + query);
                    }
                }
                default -> {
                    Label lbl = new Label("Ingen giltig sökkategori vald.");
                    searchResultsBox.getChildren().add(lbl);
                }
            }
        } catch (SQLException e) {
            Label lbl = new Label("Fel vid databasfråga: " + e.getMessage());
            searchResultsBox.getChildren().add(lbl);
        } catch (NumberFormatException e) {
            Label lbl = new Label("Fel: Skriv ett giltigt heltal.");
            searchResultsBox.getChildren().add(lbl);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        SceneManager.showLoginView();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        SceneManager.showRegisterView();
    }
    @FXML
    @Override
    public void setData(Object data) {
        if (data instanceof User user) {
            setCurrentUser(user);
            System.out.println("Inloggad som: " + user.getFirstName());
        }
    }
}