package game.arkanoid.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load giao diện từ file FXML
        System.out.println(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource(
                "/game/arkanoid/fxml/LoginView.fxml"));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
