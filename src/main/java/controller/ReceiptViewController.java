package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReceiptViewController {

    @FXML
    private TextArea receiptTextArea;

    public void setReceiptContent(String content) {
        receiptTextArea.setText(content);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) receiptTextArea.getScene().getWindow();
        stage.close();
    }
}