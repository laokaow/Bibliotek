package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StartController {

    @FXML private Label lblHeader;
    @FXML private Button btnStart;
    @FXML private Button btnServices;
    @FXML private Button btnInspiration;
    @FXML private Button btnMedia;
    @FXML private Button btnLiterature;
    @FXML private Button btnHelp;
    @FXML private Button btnAccount;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbSearchCategory;
    @FXML private Button btnSearch;
    @FXML private Label lblDiscover;
    @FXML private Label lblBookTips;
    @FXML private Button btnSeeMoreBooks;
    @FXML private Label lblEvents;
    @FXML private Button btnSeeMoreEvents;
    @FXML private Label lblMediaCulture;
    @FXML private Button btnSeeMoreMedia;
    @FXML private Label lblFooter;

    // Funktionalitet implementeras här
    @FXML
    private void initialize() {
        // Exempel: fyll sökkategorier
        cmbSearchCategory.getItems().addAll("Böcker", "Media", "Evenemang");
    }
}
