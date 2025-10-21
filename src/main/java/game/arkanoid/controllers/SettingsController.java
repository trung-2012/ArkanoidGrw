package game.arkanoid.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import game.arkanoid.utils.GameSettings;


public class SettingsController {

    // Các ImageView trong Settings
    @FXML
    private ImageView ballImageView;
    @FXML
    private ImageView paddleImageView;

    @FXML
    private ImageView leftBallImageView;
    @FXML
    private ImageView rightBallImageView;
    @FXML
    private ImageView leftPaddleImageView;
    @FXML
    private ImageView rightPaddleImageView;

    @FXML
    private ImageView saveImageView;
    @FXML
    private ImageView confirmBallImageView;
    @FXML
    private ImageView confirmPaddleImageView;

    // Skin
    private final String[] ballSkins = {
            "/game/arkanoid/images/Ball.png",
            "/game/arkanoid/images/Paddle.png"
    };
    private final String[] paddleSkins = {
            "/game/arkanoid/images/Paddle.png",
            "/game/arkanoid/images/Ball.png"
    };

    private int ballIndex = 0;
    private int paddleIndex = 0;

    //Hover Event
    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "saveButton":
                saveImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/save.png").toExternalForm()));
                break;
            case "confirmBallButton":
                confirmBallImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/change c.png").toExternalForm()));
                break;
            case "confirmPaddleButton":
                confirmPaddleImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/change c.png").toExternalForm()));
                break;
            case "leftBallButton":
                leftBallImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left c.png").toExternalForm()));
                break;
            case "rightBallButton":
                rightBallImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right c.png").toExternalForm()));
                break;
            case "leftPaddleButton":
                leftPaddleImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left c.png").toExternalForm()));
                break;
            case "rightPaddleButton":
                rightPaddleImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right c.png").toExternalForm()));
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "saveButton":
                saveImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/save.png").toExternalForm()));
                break;
            case "confirmBallButton":
                confirmBallImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/change.png").toExternalForm()));
                break;
            case "confirmPaddleButton":
                confirmPaddleImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/change.png").toExternalForm()));
                break;
            case "leftBallButton":
                leftBallImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left.png").toExternalForm()));
                break;
            case "rightBallButton":
                rightBallImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right.png").toExternalForm()));
                break;
            case "leftPaddleButton":
                leftPaddleImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left.png").toExternalForm()));
                break;
            case "rightPaddleButton":
                rightPaddleImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right.png").toExternalForm()));
                break;
        }
    }

    @FXML private void prevBall() {
        ballIndex = (ballIndex - 1 + ballSkins.length) % ballSkins.length;
        updateBallImage();
    }

    @FXML
    private void nextBall() {
        ballIndex = (ballIndex + 1) % ballSkins.length;
        updateBallImage();
    }

    @FXML
    private void prevPaddle() {
        paddleIndex = (paddleIndex - 1 + paddleSkins.length) % paddleSkins.length;
        updatePaddleImage();
    }

    @FXML
    private void nextPaddle() {
        paddleIndex = (paddleIndex + 1) % paddleSkins.length;
        updatePaddleImage();
    }

    @FXML
    private void confirmBall() {
        System.out.println("Ball đã chọn: " + ballSkins[ballIndex]);
        GameSettings.setSelectedBall(ballSkins[ballIndex]); // 🟢 Lưu lại skin
    }

    @FXML
    private void confirmPaddle() {
        System.out.println("Paddle đã chọn: " + paddleSkins[paddleIndex]);
        GameSettings.setSelectedPaddle(paddleSkins[paddleIndex]); // 🟣 Lưu lại skin
    }

    @FXML private void saveSettings(ActionEvent event) {
        System.out.println(" Đã lưu:");
        System.out.println("- Ball: " + ballSkins[ballIndex]);
        System.out.println("- Paddle: " + paddleSkins[paddleIndex]);
        goBackToMenu(event);
    }

    private void goBackToMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update Images
    private void updateBallImage() {
        ballImageView.setImage(new Image(getClass().getResource(ballSkins[ballIndex]).toExternalForm()));
    }

    private void updatePaddleImage() {
        paddleImageView.setImage(new Image(getClass().getResource(paddleSkins[paddleIndex]).toExternalForm()));
    }
}
