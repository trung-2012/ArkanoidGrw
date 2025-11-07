package game.arkanoid.controllers;

import game.arkanoid.models.Brick;
import game.arkanoid.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.List;

public class PreviewController {

    @FXML
    private Label levelLabel;

    @FXML
    private Canvas previewCanvas;

    @FXML
    private ImageView leftImageView;

    @FXML
    private ImageView rightImageView;

    @FXML
    private ImageView backImageView;

    private int levelIndex = 0;

    //File level (từ folder levels)
    private final String[] levelFiles = {
            "level1.txt",
            "level2.txt",
            "level3.txt",
            "level4.txt"
    };

    @FXML
    private void initialize() {
        updatePreview();
    }

    private void updatePreview() {
        levelLabel.setText("Level " + (levelIndex + 1));
        renderLevel();
    }

    //level + vẽ
    private void renderLevel() {
        GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());

        List<Brick> bricks = LevelLoader.loadLevel(levelFiles[levelIndex]);

        double scale = 0.55; //tỉ lệ khung

        for (Brick b : bricks) {
            Image img = b.getBrickImage();
            if (img == null) continue;

            double drawX = b.getPosition().getX() * scale;
            double drawY = b.getPosition().getY() * scale;

            double drawW = b.getWidth() * scale;
            double drawH = b.getHeight() * scale;

            gc.drawImage(img, drawX, drawY, drawW, drawH);
        }
    }

    @FXML
    private void prevLevel() {
        levelIndex = (levelIndex - 1 + levelFiles.length) % levelFiles.length;
        updatePreview();
    }

    @FXML
    private void nextLevel() {
        levelIndex = (levelIndex + 1) % levelFiles.length;
        updatePreview();
    }

    // Hover hiệu ứng sáng
    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Node node = (Node) event.getSource();
        switch (node.getId()) {
            case "leftButton" ->
                    leftImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left c.png").toExternalForm()));
            case "rightButton" ->
                    rightImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right c.png").toExternalForm()));
            case "backButton" ->
                    backImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/back c.png").toExternalForm()));
        }
    }

    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        Node node = (Node) event.getSource();
        switch (node.getId()) {
            case "leftButton" ->
                    leftImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/left.png").toExternalForm()));
            case "rightButton" ->
                    rightImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/right.png").toExternalForm()));
            case "backButton" ->
                    backImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/back.png").toExternalForm()));
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
