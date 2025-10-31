package game.arkanoid.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import game.arkanoid.sound.SoundManager;

public class StartMenuController {

    @FXML
    private ImageView startImageView;
    @FXML
    private ImageView settingsImageView;
    @FXML
    private ImageView exitImageView;

    @FXML
    public void initialize() {
        if (SoundManager.isSoundEnabled()) {
            if (!SoundManager.isMenuMusicPlaying()) {
                SoundManager.playMenuMusic();
            }
        } else {
            SoundManager.stopAll();
        }
    }

    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "startButton":
                startImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm()));
                break;
            case "settingsButton":
                settingsImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/settings c.png").toExternalForm()));
                break;
            case "exitButton":
                exitImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/exit c.png").toExternalForm()));
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "startButton":
                startImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/start.png").toExternalForm()));
                break;
            case "settingsButton":
                settingsImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/settings.png").toExternalForm()));
                break;
            case "exitButton":
                exitImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/exit.png").toExternalForm()));
                break;
        }
    }

    @FXML
    private void startGame(ActionEvent event) {
        try {
            SoundManager.stopAll();
            if (SoundManager.isSoundEnabled()) SoundManager.playGameMusic();

            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/MainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/game/arkanoid/css/neon.css").toExternalForm());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSettings(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/game/arkanoid/css/neon.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitGame() {
        SoundManager.stopAll();
        System.exit(0);
    }
}
