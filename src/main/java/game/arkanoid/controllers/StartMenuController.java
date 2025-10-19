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
    @FXML
    private javafx.scene.image.ImageView startImageView;
    @FXML
    private javafx.scene.image.ImageView settingsImageView;
    @FXML
    private javafx.scene.image.ImageView exitImageView;

    // Xử lý sự kiện khi di chuột vào button
    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image hoverImage = null;
        
        switch (buttonId) {
            case "startButton":
                hoverImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hoverImage);
                break;
            case "settingsButton":
                hoverImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/settings c.png").toExternalForm());
                settingsImageView.setImage(hoverImage);
                break;
            case "exitButton":
                hoverImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/exit c.png").toExternalForm());
                exitImageView.setImage(hoverImage);
                break;
        }
    }

    // Xử lý sự kiện khi di chuột ra khỏi button
    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image normalImage = null;
        
        switch (buttonId) {
            case "startButton":
                normalImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normalImage);
                break;
            case "settingsButton":
                normalImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/settings.png").toExternalForm());
                settingsImageView.setImage(normalImage);
                break;
            case "exitButton":
                normalImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/exit.png").toExternalForm());
                exitImageView.setImage(normalImage);
                break;
        }
    }

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
