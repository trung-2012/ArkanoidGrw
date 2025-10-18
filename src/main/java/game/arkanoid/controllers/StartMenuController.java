package game.arkanoid.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class StartMenuController {
    // Bắt đầu trò chơi
    @FXML
    private void startGame(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/game/arkanoid/fxml/MainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mở cửa sổ cài đặt (update...)
    @FXML
    private void openSettings() {
        System.out.println("Open settings");
    }

    // Thoát khỏi ứng dụng hoàn toàn.
    @FXML
    private void exitGame() {
        System.out.println("Exit game");
        System.exit(0);
    }
}
