package game.arkanoid.controllers;

import game.arkanoid.player_manager.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PreviewGameController {

    @FXML
    private ImageView previewImageView;
    @FXML
    private ImageView leftImageView;
    @FXML
    private ImageView rightImageView;
    @FXML
    private ImageView backImageView;
    @FXML
    private Label levelLabel;
    private Player currentPlayer;

    public void setPlayer(Player p) {
        this.currentPlayer = p;
    }

    private final String[] levelPreviews = {
            "/game/arkanoid/images/prev1.png",
            "/game/arkanoid/images/prev2.png",
            "/game/arkanoid/images/prev3.png",
            "/game/arkanoid/images/prev4.png"
    };

    private int currentIndex = 0;

    @FXML
    private void initialize() {
        updatePreview();
    }

    @FXML
    private void prevLevel() {
        currentIndex = (currentIndex - 1 + levelPreviews.length) % levelPreviews.length;
        updatePreview();
    }

    @FXML
    private void nextLevel() {
        currentIndex = (currentIndex + 1) % levelPreviews.length;
        updatePreview();
    }

    private void updatePreview() {
        previewImageView.setImage(new Image(getClass().getResource(levelPreviews[currentIndex]).toExternalForm()));
        levelLabel.setText("Level " + (currentIndex + 1));
        
        // Update label color and glow effect based on level
        String style;
        switch (currentIndex) {
            case 0: // Level 1: Blue
                style = "-fx-text-fill: #3399ff; -fx-font-size: 72px; -fx-underline: true; -fx-effect: dropshadow(gaussian, #3399ff, 5, 0.8, 0, 0);";
                break;
            case 1: // Level 2: Orange
                style = "-fx-text-fill: #ff9933; -fx-font-size: 72px; -fx-underline: true; -fx-effect: dropshadow(gaussian, #ff9933, 5, 0.8, 0, 0);";
                break;
            case 2: // Level 3: Red
                style = "-fx-text-fill: #ff3333; -fx-font-size: 72px; -fx-underline: true; -fx-effect: dropshadow(gaussian, #ff3333, 5, 0.8, 0, 0);";
                break;
            case 3: // Level 4: Light Purple
                style = "-fx-text-fill: #cc99ff; -fx-font-size: 72px; -fx-underline: true; -fx-effect: dropshadow(gaussian, #cc99ff, 5, 0.8, 0, 0);";
                break;
            default:
                style = "-fx-text-fill: white; -fx-font-size: 72px; -fx-underline: true;";
                break;
        }
        levelLabel.setStyle(style);
    }

    @FXML
    private void backToSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Parent root = loader.load();

            SettingsController controller = loader.getController();

            controller.setPlayer(currentPlayer);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Xử lý hiệu ứng hover cho các nút
    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        switch (btn.getId()) {
            case "leftButton":
                leftImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left c.png").toExternalForm()));
                break;
            case "rightButton":
                rightImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right c.png").toExternalForm()));
                break;
            case "backButton":
                backImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/back c.png").toExternalForm()));
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        switch (btn.getId()) {
            case "leftButton":
                leftImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left.png").toExternalForm()));
                break;
            case "rightButton":
                rightImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right.png").toExternalForm()));
                break;
            case "backButton":
                backImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/back.png").toExternalForm()));
                break;
        }
    }
}
