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

    private MainController mainController;
    private boolean openedFromPause = false;
    public void setOpenedFromPause(boolean v) { openedFromPause = v; }
    public void setMainController(MainController c) { mainController = c; }

    @FXML private Slider volumeSlider;
    @FXML private ImageView ballImageView, paddleImageView, soundToggleImage;
    @FXML private ImageView leftBallImageView, rightBallImageView, leftPaddleImageView, rightPaddleImageView;
    @FXML private ImageView saveImageView, confirmBallImageView, confirmPaddleImageView;

    private final String[] ballSkins = {
            "/game/arkanoid/images/Ball.png", "/game/arkanoid/images/Ball1.png",
            "/game/arkanoid/images/Ball2.png", "/game/arkanoid/images/Ball3.png",
            "/game/arkanoid/images/Ball4.png"
    };

    private final String[] paddleSkins = {
            "/game/arkanoid/images/Paddle.png", "/game/arkanoid/images/Paddle1.png",
            "/game/arkanoid/images/Paddle2.png", "/game/arkanoid/images/Paddle3.png",
            "/game/arkanoid/images/Paddle4.png", "/game/arkanoid/images/Paddle5.png"
    };

    private int ballIndex = 0, paddleIndex = 0;
    private static final String SOUND_ON = "/game/arkanoid/images/sound_on.png";
    private static final String SOUND_OFF = "/game/arkanoid/images/sound_off.png";

    @FXML
    public void initialize() {
        volumeSlider.setFocusTraversable(false);
        soundToggleImage.setCursor(javafx.scene.Cursor.HAND);

        updateSoundIcon();
        volumeSlider.setValue(SoundManager.getVolume());
        volumeSlider.setDisable(!SoundManager.isSoundEnabled());
        volumeSlider.valueProperty().addListener((o, ov, nv) -> {
            if (SoundManager.isSoundEnabled()) SoundManager.setVolume(nv.doubleValue());
        });

        ballIndex = find(ballSkins, GameSettings.getSelectedBall());
        paddleIndex = find(paddleSkins, GameSettings.getSelectedPaddle());
        updateBall(); updatePaddle();
    }

    private int find(String[] a, String v) { for (int i = 0; i < a.length; i++) if (a[i].equals(v)) return i; return 0; }

    @FXML private void toggleSound(ActionEvent e) {
        boolean en = !SoundManager.isSoundEnabled();
        SoundManager.setSoundEnabled(en);
        if (en) SoundManager.playMenuMusic(); else SoundManager.stopMenuMusic();
        updateSoundIcon(); volumeSlider.setDisable(!en);
    }

    private void updateSoundIcon() {
        soundToggleImage.setImage(new Image(get(SoundManager.isSoundEnabled() ? SOUND_ON : SOUND_OFF)));
    }

    private String get(String p) { return getClass().getResource(p).toExternalForm(); }

    @FXML private void prevBall()    { ballIndex = (ballIndex - 1 + ballSkins.length) % ballSkins.length; updateBall(); }
    @FXML private void nextBall()    { ballIndex = (ballIndex + 1) % ballSkins.length; updateBall(); }
    @FXML private void prevPaddle() { paddleIndex = (paddleIndex - 1 + paddleSkins.length) % paddleSkins.length; updatePaddle(); }
    @FXML private void nextPaddle() { paddleIndex = (paddleIndex + 1) % paddleSkins.length; updatePaddle(); }

    private void updateBall()   { ballImageView.setImage(new Image(get(ballSkins[ballIndex]))); }
    private void updatePaddle() { paddleImageView.setImage(new Image(get(paddleSkins[paddleIndex]))); }

    @FXML private void confirmBall()    { GameSettings.setSelectedBall(ballSkins[ballIndex]); if (mainController != null) mainController.reloadGameSkins(); }
    @FXML private void confirmPaddle() { GameSettings.setSelectedPaddle(paddleSkins[paddleIndex]); if (mainController != null) mainController.reloadGameSkins(); }

    @FXML private void onButtonMouseEntered(MouseEvent e) {
        Button b = (Button)e.getSource();
        switch (b.getId()) {
            case "saveButton" -> saveImageView.setImage(new Image(get("/game/arkanoid/images/save c.png")));
            case "confirmBallButton" -> confirmBallImageView.setImage(new Image(get("/game/arkanoid/images/change c.png")));
            case "confirmPaddleButton" -> confirmPaddleImageView.setImage(new Image(get("/game/arkanoid/images/change c.png")));
            case "leftBallButton" -> leftBallImageView.setImage(new Image(get("/game/arkanoid/images/left c.png")));
            case "rightBallButton" -> rightBallImageView.setImage(new Image(get("/game/arkanoid/images/right c.png")));
            case "leftPaddleButton" -> leftPaddleImageView.setImage(new Image(get("/game/arkanoid/images/left c.png")));
            case "rightPaddleButton" -> rightPaddleImageView.setImage(new Image(get("/game/arkanoid/images/right c.png")));
        }
    }

    @FXML private void onButtonMouseExited(MouseEvent e) {
        Button b = (Button)e.getSource();
        switch (b.getId()) {
            case "saveButton" -> saveImageView.setImage(new Image(get("/game/arkanoid/images/save.png")));
            case "confirmBallButton" -> confirmBallImageView.setImage(new Image(get("/game/arkanoid/images/change.png")));
            case "confirmPaddleButton" -> confirmPaddleImageView.setImage(new Image(get("/game/arkanoid/images/change.png")));
            case "leftBallButton" -> leftBallImageView.setImage(new Image(get("/game/arkanoid/images/left.png")));
            case "rightBallButton" -> rightBallImageView.setImage(new Image(get("/game/arkanoid/images/right.png")));
            case "leftPaddleButton" -> leftPaddleImageView.setImage(new Image(get("/game/arkanoid/images/left.png")));
            case "rightPaddleButton" -> rightPaddleImageView.setImage(new Image(get("/game/arkanoid/images/right.png")));
        }
    }

    @FXML private void saveSettings(ActionEvent e) {
        GameSettings.setSelectedBall(ballSkins[ballIndex]);
        GameSettings.setSelectedPaddle(paddleSkins[paddleIndex]);

        if (mainController != null && openedFromPause) {
            mainController.reloadGameSkins();
            ((Stage)((Node)e.getSource()).getScene().getWindow()).close();
            return;
        }

        if (mainController != null) {
            mainController.reloadGameSkins();
            return;
        }

        try {
            Stage st = (Stage)((Node)e.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
            st.setScene(new Scene(root, 800, 600));
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}
