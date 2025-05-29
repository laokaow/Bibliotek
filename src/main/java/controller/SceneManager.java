package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public interface ControlledScene {
        void setData(Object data) throws SQLException;
    }

    public static void showScene(String fxmlFile) {
        loadScene(fxmlFile, null);
    }

    public static void showScene(String fxmlFile, Object data) {
        loadScene(fxmlFile, data);
    }

    private static void loadScene(String fxmlFile, Object data) {
        try {
            URL resource = SceneManager.class.getResource("/view/" + fxmlFile);
            if (resource == null) throw new RuntimeException("FXML hittades inte: " + fxmlFile);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ControlledScene && data != null) {
                ((ControlledScene) controller).setData(data);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Kunde inte ladda scen: " + fxmlFile, e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void openNewStage(String fxmlFile, String title, Object data) {
        try {
            URL resource = SceneManager.class.getResource("/view/" + fxmlFile);
            if (resource == null) throw new RuntimeException("FXML hittades inte: " + fxmlFile);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ControlledScene && data != null) {
                ((ControlledScene) controller).setData(data);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initOwner(primaryStage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Kunde inte öppna ny scen: " + fxmlFile, e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showMainView() {
        showScene("Main.fxml");
    }

    public static void showLoginView() {
        showScene("LoginView.fxml");
    }

    public static void showRegisterView() {
        showScene("RegisterView.fxml");
    }

    public static void showProfileView(Object userData) {
        showScene("ProfileView.fxml", userData);
    }
}