package game.arkanoid.controllers;

import javafx.scene.control.Slider;
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

import game.arkanoid.sound.SoundManager;
import game.arkanoid.utils.GameSettings;

public class SettingsController {

    @FXML private Slider volumeSlider;

    @FXML private ImageView ballImageView;
    @FXML private ImageView paddleImageView;
    @FXML private ImageView soundToggleImage;

    @FXML private ImageView leftBallImageView;
    @FXML private ImageView rightBallImageView;
    @FXML private ImageView leftPaddleImageView;
    @FXML private ImageView rightPaddleImageView;

    @FXML private ImageView saveImageView;
    @FXML private ImageView confirmBallImageView;
    @FXML private ImageView confirmPaddleImageView;

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

    private static final String SOUND_ON = "/game/arkanoid/images/sound_on.png";
    private static final String SOUND_OFF = "/game/arkanoid/images/sound_off.png";

    @FXML
    public void initialize() {
        volumeSlider.setFocusTraversable(false);
        soundToggleImage.setCursor(javafx.scene.Cursor.HAND);

        updateSoundIcon();

        volumeSlider.setValue(SoundManager.getVolume());
        volumeSlider.setDisable(!SoundManager.isSoundEnabled());

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (SoundManager.isSoundEnabled()) {
                SoundManager.setVolume(newVal.doubleValue());
            }
        });
    }

    @FXML
    private void toggleSound(ActionEvent event) {
        if (SoundManager.isSoundEnabled()) {
            SoundManager.stopMenuMusic();
            SoundManager.setSoundEnabled(false);
        } else {
            SoundManager.setSoundEnabled(true);
            SoundManager.playMenuMusic();
        }

        updateSoundIcon();
        volumeSlider.setDisable(!SoundManager.isSoundEnabled());
    }

    private void updateSoundIcon() {
        String path = SoundManager.isSoundEnabled() ? SOUND_ON : SOUND_OFF;
        soundToggleImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
    }

    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getId()) {
            case "saveButton" -> saveImageView.setImage(new Image(get("save c.png")));
            case "confirmBallButton" -> confirmBallImageView.setImage(new Image(get("change c.png")));
            case "confirmPaddleButton" -> confirmPaddleImageView.setImage(new Image(get("change c.png")));
            case "leftBallButton" -> leftBallImageView.setImage(new Image(get("left c.png")));
            case "rightBallButton" -> rightBallImageView.setImage(new Image(get("right c.png")));
            case "leftPaddleButton" -> leftPaddleImageView.setImage(new Image(get("left c.png")));
            case "rightPaddleButton" -> rightPaddleImageView.setImage(new Image(get("right c.png")));
        }
    }

    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getId()) {
            case "saveButton" -> saveImageView.setImage(new Image(get("save.png")));
            case "confirmBallButton" -> confirmBallImageView.setImage(new Image(get("change.png")));
            case "confirmPaddleButton" -> confirmPaddleImageView.setImage(new Image(get("change.png")));
            case "leftBallButton" -> leftBallImageView.setImage(new Image(get("left.png")));
            case "rightBallButton" -> rightBallImageView.setImage(new Image(get("right.png")));
            case "leftPaddleButton" -> leftPaddleImageView.setImage(new Image(get("left.png")));
            case "rightPaddleButton" -> rightPaddleImageView.setImage(new Image(get("right.png")));
        }
    }

    private String get(String file) {
        return "/game/arkanoid/images/" + file;
    }

    @FXML private void prevBall() { ballIndex = (ballIndex - 1 + ballSkins.length) % ballSkins.length; updateBall(); }

    @FXML private void nextBall() { ballIndex = (ballIndex + 1) % ballSkins.length; updateBall(); }

    @FXML private void prevPaddle() { paddleIndex = (paddleIndex - 1 + paddleSkins.length) % paddleSkins.length; updatePaddle(); }

    @FXML private void nextPaddle() { paddleIndex = (paddleIndex + 1) % paddleSkins.length; updatePaddle(); }

    private void updateBall() {
        ballImageView.setImage(new Image(getClass().getResource(ballSkins[ballIndex]).toExternalForm()));
    }

    private void updatePaddle() {
        paddleImageView.setImage(new Image(getClass().getResource(paddleSkins[paddleIndex]).toExternalForm()));
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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/game/arkanoid/css/neon.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
