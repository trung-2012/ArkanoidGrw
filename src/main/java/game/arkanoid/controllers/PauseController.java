package game.arkanoid.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class PauseController {

    private MainController mainController;
    private Stage pauseStage;

    @FXML
    private Button resumeButton;
    @FXML
    private ImageView resumeImageView;

    public void setMainController(MainController c) {
        mainController = c;
    }

    public void setPauseStage(Stage s) {
        pauseStage = s;
    }

    @FXML
    public void initialize() {
        FadeTransition fade = new FadeTransition(Duration.millis(300));
        fade.setFromValue(0);
        fade.setToValue(1);
        if (resumeImageView.getScene() != null) {
            fade.setNode(resumeImageView.getScene().getRoot());
            fade.play();
        }
    }

    @FXML
    private void onResumeMouseEntered() {
        resumeImageView.setImage(new Image(get("/game/arkanoid/images/resume c.png")));
    }

    @FXML
    private void onResumeMouseExited() {
        resumeImageView.setImage(new Image(get("/game/arkanoid/images/resume.png")));
    }

    @FXML
    private void handleResume(ActionEvent e) {
        if (mainController != null) mainController.resumeGame();
        pauseStage.close();
    }

    @FXML
    private void handleRestart(ActionEvent e) {
        if (mainController != null) mainController.resetGameFromPause();
        pauseStage.close();
    }

    @FXML
    private void handleSettings(ActionEvent e) {
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Parent root = l.load();
            SettingsController sc = l.getController();
            sc.setMainController(mainController);
            sc.setOpenedFromPause(true);

            Stage ss = new Stage();
            ss.initOwner(pauseStage);
            ss.initModality(javafx.stage.Modality.WINDOW_MODAL);

            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(get("/game/arkanoid/css/neon.css"));
            ss.setScene(scene);
            ss.show();

            ss.setOnHidden(ev -> {
                if (mainController != null) mainController.reloadGameSkins();
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleMainMenu(ActionEvent e) {
        if (mainController != null) mainController.returnToMenuFromPause();
        pauseStage.close();
    }

    private String get(String p) {
        return getClass().getResource(p).toExternalForm();
    }
}
