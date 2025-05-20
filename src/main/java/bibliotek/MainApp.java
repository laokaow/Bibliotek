package bibliotek;
import controller.SceneManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            SceneManager.setStage(primaryStage);
            SceneManager.showMainView();
            primaryStage.setTitle("Bibliotek.se");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/view/icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}