package bibliotek;

import controller.SceneManager;
import dao.DAOFactory;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.DatabaseConnection;

import java.util.Objects;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        try {
            // Gemensam databasanslutning
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
            primaryStage.setTitle("Norrbottens Bibliotek");
            //Objects.requireNonNull för att ta bort varning
            //Vid iterationer på koden så krävs felhantering
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/view/icon.png"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}