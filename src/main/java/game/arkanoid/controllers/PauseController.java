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

    @FXML private Button resumeButton;
    @FXML private ImageView resumeImageView;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPauseStage(Stage pauseStage) {
        this.pauseStage = pauseStage;
    }

    @FXML
    public void initialize() {
        // Fade-in nhẹ khi mở pause menu
        FadeTransition fade = new FadeTransition(Duration.millis(300));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(resumeImageView.getScene() != null ? resumeImageView.getScene().getRoot() : null);
        fade.play();
    }

    // Hover effect cho Resume
    @FXML
    private void onResumeMouseEntered() {
        Image hoverImage = new Image(getClass()
                .getResource("/game/arkanoid/images/resume c.png").toExternalForm());
        resumeImageView.setImage(hoverImage);
    }

    @FXML
    private void onResumeMouseExited() {
        Image normalImage = new Image(getClass()
                .getResource("/game/arkanoid/images/resume.png").toExternalForm());
        resumeImageView.setImage(normalImage);
    }

    // Resume game
    @FXML
    private void handleResume(ActionEvent event) {
        if (mainController != null) {
            mainController.resumeGame();
        }
        pauseStage.close();
    }

    // Restart game
    @FXML
    private void handleRestart(ActionEvent event) {
        if (mainController != null) {
            mainController.resetGameFromPause();
        }
        pauseStage.close();
    }

    // Open Settings
    @FXML
    private void handleSettings(ActionEvent event) {
        pauseStage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            Stage settingsStage = new Stage();
            settingsController.setSettingsStage(settingsStage);
            settingsController.setMainController(mainController);

            Stage mainStage = (Stage) pauseStage.getOwner();

            settingsStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            settingsStage.initOwner(mainStage);
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(root, 800, 600));
            settingsStage.setResizable(false);

            settingsStage.setOnHidden(e -> {
                if (pauseStage != null && !pauseStage.isShowing()) {
                    pauseStage.show();
                }
            });

            settingsStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Back to main menu
    @FXML
    private void handleMainMenu(ActionEvent event) {
        if (mainController != null) {
            mainController.returnToMenuFromPause();
        }
        pauseStage.close();
    }
}
