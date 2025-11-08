package game.arkanoid.controllers;

import game.arkanoid.models.Player;
import game.arkanoid.views.GameEngine;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button pauseButton;
    @FXML
    private ImageView pauseImageView;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label livesLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private HBox topBar;
    @FXML
    private HBox bottomBar;
    @FXML
    private StackPane rootStack;
    @FXML
    private BorderPane mainGamePane;
    @FXML
    private Rectangle pauseOverlay;

    private Player player;
    private GameEngine engine;
    private Stage pauseStage;

    private boolean isPaused = false;

    public void setPlayer(Player player) {
        this.player = player;
        if (playerNameLabel != null && player != null) {
            playerNameLabel.setText(player.getNickname());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = new GameEngine();
        engine.setMainController(this);
        engine.initializeGame(gameCanvas, scoreLabel, livesLabel, levelLabel);
        updateBackgroundForLevel(1);
        levelLabel.setText("Level: " + engine.getCurrentLevel());

        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImageView.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImageView.fitHeightProperty().bind(newScene.heightProperty());

                newScene.setOnKeyPressed(e -> {
                    switch (e.getCode()) {
                        case LEFT:
                        case A:
                            engine.setLeftPressed(true);
                            break;
                        case RIGHT:
                        case D:
                            engine.setRightPressed(true);
                            break;
                        case SPACE:
                            engine.handleSpacePressed();
                            break;
                    }
                });

                newScene.setOnKeyReleased(e -> {
                    switch (e.getCode()) {
                        case LEFT:
                        case A:
                            engine.setLeftPressed(false);
                            break;
                        case RIGHT:
                        case D:
                            engine.setRightPressed(false);
                            break;
                    }
                });

                gameCanvas.widthProperty().bind(newScene.widthProperty());
                gameCanvas.heightProperty().bind(newScene.heightProperty()
                        .subtract(topBar.heightProperty())
                        .subtract(bottomBar.heightProperty()));

                javafx.application.Platform.runLater(() -> {
                    engine.startNewGame();
                    gameCanvas.requestFocus();
                });
            }
        });
    }

    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        pauseImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/pause c.png").toExternalForm()));
    }

    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        pauseImageView.setImage(new Image(getClass().getResource("/game/arkanoid/images/pause.png").toExternalForm()));
    }

    public void updateBackgroundForLevel(int level) {
        String path = String.format("/game/arkanoid/images/MapLevel%d.png", level);
        backgroundImageView.setImage(new Image(getClass().getResource(path).toExternalForm()));
    }

    @FXML
    private void pauseGame(ActionEvent event) {
        if (!isPaused) {
            isPaused = true;
            engine.setGameRunning(false);
            applyPauseBlur();
            showPauseMenu();
        }
        gameCanvas.requestFocus();
    }

    public void applyPauseBlur() {
        mainGamePane.setEffect(new GaussianBlur(10));
        pauseOverlay.setVisible(true);
        pauseOverlay.setOpacity(1);
    }

    public void showPauseMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/PauseView.fxml"));
            Parent root = loader.load();
            PauseController pauseController = loader.getController();
            pauseController.setMainController(this);

            pauseStage = new Stage();
            pauseController.setPauseStage(pauseStage);

            pauseStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            pauseStage.initOwner(gameCanvas.getScene().getWindow());
            pauseStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root, 400, 300);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            pauseStage.setScene(scene);

            Stage mainStage = (Stage) gameCanvas.getScene().getWindow();
            pauseStage.setX(mainStage.getX() + (800 - 400) / 2);
            pauseStage.setY(mainStage.getY() + (600 - 300) / 2);

            pauseStage.setOnHidden(e -> {
                if (!isPaused) {
                    mainGamePane.setEffect(null);
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pauseOverlay);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(ev -> pauseOverlay.setVisible(false));
                    fadeOut.play();
                }
            });

            pauseStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resumeGame() {
        isPaused = false;
        engine.setGameRunning(true);
        mainGamePane.setEffect(null);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pauseOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(ev -> pauseOverlay.setVisible(false));
        fadeOut.play();

        gameCanvas.requestFocus();
    }

    public void reloadGameSkins() {
        if (engine != null) engine.reloadSkins();
    }

    public void resetGameFromPause() {
        isPaused = false;
        engine.resetCurrentLevel();

        mainGamePane.setEffect(null);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pauseOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(ev -> pauseOverlay.setVisible(false));
        fadeOut.play();

        gameCanvas.requestFocus();
    }

    public void returnToMenuFromPause() {
        isPaused = false;
        engine.setGameRunning(false);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
