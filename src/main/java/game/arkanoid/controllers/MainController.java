package game.arkanoid.controllers;

import game.arkanoid.views.GameEngine;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label livesLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private javafx.scene.image.ImageView backgroundImageView;
    @FXML
    private javafx.scene.layout.HBox topBar;
    @FXML
    private javafx.scene.layout.HBox bottomBar;

    private GameEngine engine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = new GameEngine();
        engine.initializeGame(gameCanvas);
        // attach key listeners when scene is ready
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                    // bind background image size to scene size
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
                // bind background image size to scene size
                backgroundImageView.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImageView.fitHeightProperty().bind(newScene.heightProperty());

                // bind canvas size to available center area
                gameCanvas.widthProperty().bind(newScene.widthProperty());
                gameCanvas.heightProperty().bind(newScene.heightProperty().subtract(topBar.heightProperty()).subtract(bottomBar.heightProperty()));
                // initialize game after layout pass so canvas has actual size
                javafx.application.Platform.runLater(() -> {
                    engine.startNewGame();
                    gameCanvas.requestFocus();
                });
            }
        });
    }

    @FXML
    private void startGame(ActionEvent event) {
        engine.setGameRunning(true);
    }

    @FXML
    private void pauseGame(ActionEvent event) {
        engine.setGameRunning(!engine.isGameRunning());
    }

    @FXML
    private void resetGame(ActionEvent event) {
        engine.startNewGame();
    }

    @FXML
    private void returnToMenu(ActionEvent event) {
        // stop the game engine
        engine.setGameRunning(false);
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
