package game.arkanoid.controllers;

import game.arkanoid.sound.SoundManager; // 🎵 import để điều khiển âm thanh
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import java.io.IOException;

public class StartMenuController {
    @FXML
    private ImageView startImageView;
    @FXML
    private ImageView settingsImageView;
    @FXML
    private ImageView exitImageView;

    // ===============================
    // 🖱️ Xử lý hiệu ứng hover cho nút
    // ===============================

    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        Image hoverImage = null;

        switch (buttonId) {
            case "startButton":
                hoverImage = new Image(getClass().getResource(
                        "/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hoverImage);
                break;
            case "settingsButton":
                hoverImage = new Image(getClass().getResource(
                        "/game/arkanoid/images/settings c.png").toExternalForm());
                settingsImageView.setImage(hoverImage);
                break;
            case "exitButton":
                hoverImage = new Image(getClass().getResource(
                        "/game/arkanoid/images/exit c.png").toExternalForm());
                exitImageView.setImage(hoverImage);
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        Image normalImage = null;

        switch (buttonId) {
            case "startButton":
                normalImage = new Image(getClass().getResource(
                        "/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normalImage);
                break;
            case "settingsButton":
                normalImage = new Image(getClass().getResource(
                        "/game/arkanoid/images/settings.png").toExternalForm());
                settingsImageView.setImage(normalImage);
                break;
            case "exitButton":
                normalImage = new Image(getClass().getResource(
                        "/game/arkanoid/images/exit.png").toExternalForm());
                exitImageView.setImage(normalImage);
                break;
        }
    }

    // ===============================
    // 🚀 Bắt đầu trò chơi
    // ===============================

    @FXML
    private void startGame(ActionEvent event) {
        try {
            // 🎵 Dừng nhạc menu và phát nhạc gameplay
            SoundManager.stopMenuMusic();
            SoundManager.playGameMusic();

            Parent root = FXMLLoader.load(getClass().getResource(
                    "/game/arkanoid/fxml/MainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // ⚙️ Mở cửa sổ cài đặt
    // ===============================

    @FXML
    private void openSettings() {
        System.out.println("Open settings...");
        // Nếu sau này có file Settings.fxml → load tương tự như startGame()
    }

    // ===============================
    // ❌ Thoát trò chơi
    // ===============================

    @FXML
    private void exitGame() {
        System.out.println("Exit game");
        System.exit(0);
    }
}
