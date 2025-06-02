package controller;

import controller.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class HomeButtonController {

    @FXML
    private ImageView homeIcon;

    @FXML
    private void initialize() {
        // Ladda bilden från resources/images
        //Får varning då den kan vara null. Den är inte det.
        //Kan vara värt att hantera ifall man dynamiskt hämtar bilden
        Image image = new Image(getClass().getResourceAsStream("/view/HomeButtonIcon.png"));
        homeIcon.setImage(image);
    }

    @FXML
    private void handleHome(MouseEvent event) {
        SceneManager.showMainView();
    }
}