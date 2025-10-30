package game.arkanoid.controllers;

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

    private GameEngine engine;
    private Stage pauseStage;

    // Hover hiệu ứng cho nút Pause
    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Image hoverImage = new Image(
                getClass().getResource("/game/arkanoid/images/pause c.png").toExternalForm());
        pauseImageView.setImage(hoverImage);
    }

    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        Image normalImage = new Image(
                getClass().getResource("/game/arkanoid/images/pause.png").toExternalForm());
        pauseImageView.setImage(normalImage);
    }

    // Đổi ảnh nền theo level
    public void updateBackgroundForLevel(int level) {
        String imagePath = String.format("/game/arkanoid/images/MapLevel%d.png", level);
        backgroundImageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
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
                        default:
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
                        default:
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

    // Khi nhấn Pause
    @FXML
    private void pauseGame(ActionEvent event) {
        if (engine.isGameRunning()) {
            engine.setGameRunning(false);

            // hiệu ứng blur
            mainGamePane.setEffect(new GaussianBlur(10));

            // overlay mờ dần
            pauseOverlay.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), pauseOverlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            showPauseMenu();
        }
        gameCanvas.requestFocus();
    }

    // Hiển thị Pause menu
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

            // Khi đóng pause menu => remove blur
            pauseStage.setOnHidden(e -> {
                mainGamePane.setEffect(null);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pauseOverlay);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> pauseOverlay.setVisible(false));
                fadeOut.play();
            });

            pauseStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khi Resume
    public void resumeGame() {
        engine.setGameRunning(true);
        mainGamePane.setEffect(null);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), pauseOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pauseOverlay.setVisible(false));
        fadeOut.play();

        gameCanvas.requestFocus();
    }

    // Reload skin từ Settings
    public void reloadGameSkins() {
        if (engine != null) {
            engine.reloadSkins();
        }
    }

    public void resetGameFromPause() {
        engine.resetCurrentLevel();
        gameCanvas.requestFocus();
    }

    public void returnToMenuFromPause() {
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
