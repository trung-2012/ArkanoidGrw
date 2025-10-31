package game.arkanoid.views;

import javafx.application.Application;
import javafx.application.Platform;
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
        
        // Xử lý khi đóng cửa sổ bằng nút X
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Exit game. Đang dọn dẹp tài nguyên...");

            Platform.exit();
            
        });
        
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        System.out.println("Application đang dừng, cleanup tất cả resources...");
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
