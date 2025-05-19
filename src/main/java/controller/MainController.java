package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.event.ActionEvent;

public class MainController {


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
    private void initialize() {
        System.out.println("LibraryMainController initialized.");
        cmbSearchCategory.getItems().addAll("Böcker", "Artiklar", "Media", "Evenemang");
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText();
        String category = cmbSearchCategory.getValue();
        System.out.println("Söker efter: " + query + " i kategorin: " + category);
        cmbSearchCategory.setOnAction(e -> {
            String selectedCategory = cmbSearchCategory.getSelectionModel().getSelectedItem();
        });
        // Här kan söklogiken implementeras
    }
    @FXML
    private void handleCategorySelection(ActionEvent event) {
        String selectedCategory = cmbSearchCategory.getSelectionModel().getSelectedItem();
        System.out.println("Vald kategori: " + selectedCategory);
    }

    @FXML
    private void handleStart() {
        System.out.println("Start klickat");
    }

    @FXML
    private void handleServices() {
        System.out.println("Våra tjänster klickat");
    }

    @FXML
    private void handleInspiration() {
        System.out.println("Inspiration klickat");
    }

    @FXML
    private void handleMedia() {
        System.out.println("Media klickat");
    }

    @FXML
    private void handleLiterature() {
        System.out.println("Litteratur klickat");
    }

    @FXML
    private void handleHelp() {
        System.out.println("Hjälp klickat");
    }

    @FXML
    private void handleAccount() {
        System.out.println("Mina sidor klickat");
    }

    @FXML
    private void handleSeeMoreBooks() {
        System.out.println("Se mer böcker klickat");
    }

    @FXML
    private void handleSeeMoreEvents() {
        System.out.println("Se mer evenemang klickat");
    }

    @FXML
    private void handleSeeMoreMedia() {
        System.out.println("Se mer media klickat");
    }
}





