package game.arkanoid.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class PauseController {
    
    private MainController mainController;
    private Stage pauseStage;

    @FXML private ImageView resumeImageView;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPauseStage(Stage pauseStage) {
        this.pauseStage = pauseStage;
    }

    // Xử lí khi di chuột vào button
    @FXML
    private void onResumeMouseEntered() {
        Image hoverImage = new Image(getClass().getResource("/game/arkanoid/images/resume c.png").toExternalForm());
        resumeImageView.setImage(hoverImage);
    }

    // Xử lí khi di chuột ra khỏi button
    @FXML
    private void onResumeMouseExited() {
        Image normalImage = new Image(getClass().getResource("/game/arkanoid/images/resume.png").toExternalForm());
        resumeImageView.setImage(normalImage);
    }

    @FXML
    private void handleResume(ActionEvent event) {
        if (mainController != null) {
            mainController.resumeGame();
        }
        pauseStage.close();
    }

    @FXML
    private void handleRestart(ActionEvent event) {
        if (mainController != null) {
            mainController.resetGameFromPause();
        }
        pauseStage.close();
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        // Đóng pause menu
        pauseStage.close();
        
        // Mở Settings là 1 cửa sổ mới
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Parent root = loader.load();
            
            SettingsController settingsController = loader.getController();
            
            // Tạo Stage mới cho Settings
            Stage settingsStage = new Stage();
            settingsController.setSettingsStage(settingsStage);
            settingsController.setMainController(mainController);
            
            // Lấy owner là main game stage
            Stage mainStage = (Stage) pauseStage.getOwner();
            
            settingsStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            settingsStage.initOwner(mainStage);
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(root, 800, 600));
            settingsStage.setResizable(false);
            
            // Khi Settings đóng, hiện lại Pause menu
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

    @FXML
    private void handleMainMenu(ActionEvent event) {
        if (mainController != null) {
            mainController.returnToMenuFromPause();
        }
        pauseStage.close();
    }
}
