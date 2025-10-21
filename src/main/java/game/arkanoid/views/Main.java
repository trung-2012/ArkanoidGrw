package game.arkanoid.views;

import game.arkanoid.sound.SoundManager; // thêm phần quản lý âm thanh

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
                "/game/arkanoid/fxml/StartMenu.fxml"));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Khởi tạo hệ thống âm thanh
        SoundManager.init();
        SoundManager.playMenuMusic();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
