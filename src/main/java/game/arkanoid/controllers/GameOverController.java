package game.arkanoid.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.IOException;

public class GameOverController {
    @FXML
    private Label scoreLabel;
    @FXML
    private javafx.scene.image.ImageView restartImageView;
    @FXML
    private javafx.scene.image.ImageView menuImageView;

    // Xử lý sự kiện khi di chuột vào button
    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image hoverImage = null;

        switch (buttonId) {
            case "restartButton":
                hoverImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/PlayAgain c.png").toExternalForm());
                restartImageView.setImage(hoverImage);
                break;
            case "menuButton":
                hoverImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/MainMenu c.png").toExternalForm());
                menuImageView.setImage(hoverImage);
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
            case "restartButton":
                normalImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/PlayAgain.png").toExternalForm());
                restartImageView.setImage(normalImage);
                break;
            case "menuButton":
                normalImage = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/MainMenu.png").toExternalForm());
                menuImageView.setImage(normalImage);
                break;
        }
    }

    // Chơi lại
    @FXML
    private void restartGame(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/game/arkanoid/fxml/MainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập điểm số cuối cùng để hiển thị
    public void setFinalScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score);
        }
    }

    // Trở về menu chính
    @FXML
    private void returnToMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/game/arkanoid/fxml/StartMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
