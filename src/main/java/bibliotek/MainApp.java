package bibliotek;
import controller.SceneManager;

import dao.DAOFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.DatabaseConnection;

import java.sql.SQLException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        try {
            // Skapa en gemensam databasanslutning
            DAOFactory.init(
                    DatabaseConnection.getConnection()
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fel vid initiering av DAOFactory");
            System.exit(1);
        }
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