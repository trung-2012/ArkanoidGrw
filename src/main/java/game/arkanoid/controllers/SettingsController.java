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
import java.net.URL;

import game.arkanoid.sound.SoundManager;
import game.arkanoid.utils.GameSettings;

public class SettingsController {

    @FXML
    private ImageView ballImageView;
    @FXML
    private ImageView paddleImageView;
    @FXML
    private ImageView soundToggleImage;
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

    private final String[] ballSkins = {
            "/game/arkanoid/images/Ball.png",
            "/game/arkanoid/images/Ball1.png",
            "/game/arkanoid/images/Ball2.png",
            "/game/arkanoid/images/Ball3.png",
            "/game/arkanoid/images/Ball4.png"
    };

    private final String[] paddleSkins = {
            "/game/arkanoid/images/Paddle.png",
            "/game/arkanoid/images/Paddle1.png",
            "/game/arkanoid/images/Paddle2.png",
            "/game/arkanoid/images/Paddle3.png",
            "/game/arkanoid/images/Paddle4.png"
    };

    private int ballIndex = 0;
    private int paddleIndex = 0;

    private static final String SOUND_ON_PATH = "/game/arkanoid/images/sound_on.png";
    private static final String SOUND_OFF_PATH = "/game/arkanoid/images/sound_off.png";

    @FXML
    public void initialize() {
        soundToggleImage.setCursor(javafx.scene.Cursor.HAND);
        updateSoundIcon();
    }

    @FXML
    private void toggleSound(ActionEvent e) {
        if (SoundManager.isSoundEnabled()) {
            SoundManager.stopMenuMusic();
            SoundManager.setSoundEnabled(false);
        } else {
            SoundManager.setSoundEnabled(true);
            SoundManager.playMenuMusic();
        }
        updateSoundIcon();
    }

    private void updateSoundIcon() {
        String path = SoundManager.isSoundEnabled() ? SOUND_ON_PATH : SOUND_OFF_PATH;
        URL url = getClass().getResource(path);
        if (url != null) {
            soundToggleImage.setImage(new Image(url.toExternalForm()));
        }
    }

    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();
        switch (id) {
            case "saveButton":
                saveImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/save c.png").toExternalForm()));
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

    @FXML
    private void prevBall() {
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
        GameSettings.setSelectedBall(ballSkins[ballIndex]);
    }

    @FXML
    private void confirmPaddle() {
        GameSettings.setSelectedPaddle(paddleSkins[paddleIndex]);
    }

    @FXML
    private void saveSettings(ActionEvent event) {
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

    private void updateBallImage() {
        ballImageView.setImage(new Image(getClass().getResource(ballSkins[ballIndex]).toExternalForm()));
    }

    private void updatePaddleImage() {
        paddleImageView.setImage(new Image(getClass().getResource(paddleSkins[paddleIndex]).toExternalForm()));
    }
}
