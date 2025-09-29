package game.arkanoid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load giao diện từ file FXML
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Demo JavaFX with Scene Builder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
