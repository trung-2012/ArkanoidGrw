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
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String id = sourceButton.getId();
        Image hover = null;

        switch (id) {
            case "startButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hover);
                break;
            case "settingsButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/settings c.png").toExternalForm());
                settingsImageView.setImage(hover);
                break;
            case "exitButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/exit c.png").toExternalForm());
                exitImageView.setImage(hover);
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String id = sourceButton.getId();
        Image normal = null;

        switch (id) {
            case "startButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normal);
                break;
            case "settingsButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/settings.png").toExternalForm());
                settingsImageView.setImage(normal);
                break;
            case "exitButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/exit.png").toExternalForm());
                exitImageView.setImage(normal);
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
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSettings(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitGame() {
        System.out.println("Exit game");
        SoundManager.stopAll();
        System.exit(0);
    }
}
