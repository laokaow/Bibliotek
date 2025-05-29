package controller;

import dao.DAOFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.Media;
import dao.MediaDAO;

import java.sql.SQLException;
import java.util.List;

public class ManageMediaController {

    @FXML private TableView<Media> mediaTable;
    @FXML private TableColumn<Media, Integer> idColumn;
    @FXML private TableColumn<Media, String> nameColumn;
    @FXML private TableColumn<Media, String> typeColumn;
    @FXML private TableColumn<Media, Boolean> partOfCourseColumn;
    @FXML private TableColumn<Media, Void> deleteColumn;

    private final MediaDAO mediaDAO = DAOFactory.getMediaDAO();

    @FXML
    public void initialize() {
        System.out.println("initialize körs");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("mediaId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("mediaName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("mediaType"));

        partOfCourseColumn.setCellValueFactory(cellData ->
                new SimpleBooleanProperty(cellData.getValue().getPartOfCourse())
        );

        partOfCourseColumn.setCellFactory(CheckBoxTableCell.forTableColumn(partOfCourseColumn));
        partOfCourseColumn.setEditable(true);

        mediaTable.setEditable(true);

        partOfCourseColumn.setOnEditCommit(event -> {
            Media media = event.getRowValue();
            boolean newValue = event.getNewValue();
            media.setPartOfCourse(newValue);

            try {
                mediaDAO.updatePartOfCourse(media.getMediaId(), newValue);
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Media '" + media.getMediaName() + "' uppdaterades.");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Kunde inte uppdatera media i databasen.");
                alert.showAndWait();
                loadMedia();
            }
        });

        loadMedia();
        addDeleteButtonToTable();
    }

    private void loadMedia() {
        try {
            List<Media> mediaList = mediaDAO.getMedia();
            mediaTable.getItems().setAll(mediaList);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Kunde inte läsa media från databasen.");
            alert.showAndWait();
        }
    }

    private void addDeleteButtonToTable() {
        Callback<TableColumn<Media, Void>, TableCell<Media, Void>> deleteCellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Ta bort");

            {
                btn.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    deleteMedia(media);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };
        deleteColumn.setCellFactory(deleteCellFactory);
    }

    private void deleteMedia(Media media) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Är du säker på att du vill ta bort media: " + media.getMediaName() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                mediaDAO.deleteMedia(media.getMediaId());
                loadMedia();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alertError = new Alert(Alert.AlertType.ERROR,
                        "Kunde inte ta bort media från databasen.");
                alertError.showAndWait();
            }
        }
    }
}