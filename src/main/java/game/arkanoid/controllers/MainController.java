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
    private Button menuButton;
    @FXML
    private javafx.scene.image.ImageView startImageView;
    @FXML
    private javafx.scene.image.ImageView pauseImageView;
    @FXML
    private javafx.scene.image.ImageView resetImageView;
    @FXML
    private javafx.scene.image.ImageView menuImageView;
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
        engine.setMainController(this);
        engine.initializeGame(gameCanvas, scoreLabel, livesLabel, levelLabel);

        engine.setBallSkin(game.arkanoid.utils.GameSettings.getSelectedBall());
        engine.setPaddleSkin(game.arkanoid.utils.GameSettings.getSelectedPaddle());

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
                gameCanvas.heightProperty().bind(
                        newScene.heightProperty()
                                .subtract(topBar.heightProperty())
                                .subtract(bottomBar.heightProperty())
                );

                javafx.application.Platform.runLater(() -> {
                    engine.startNewGame();
                    gameCanvas.requestFocus();
                });
            }
        });
    }

    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image hoverImage = null;

        switch (buttonId) {
            case "startButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hoverImage);
                break;
            case "pauseButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/pause c.png").toExternalForm());
                pauseImageView.setImage(hoverImage);
                break;
            case "resetButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/reset c.png").toExternalForm());
                resetImageView.setImage(hoverImage);
                break;
            case "menuButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/MainMenu c.png").toExternalForm());
                menuImageView.setImage(hoverImage);
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image normalImage = null;

        switch (buttonId) {
            case "startButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normalImage);
                break;
            case "pauseButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/pause.png").toExternalForm());
                pauseImageView.setImage(normalImage);
                break;
            case "resetButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/reset.png").toExternalForm());
                resetImageView.setImage(normalImage);
                break;
            case "menuButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/MainMenu.png").toExternalForm());
                menuImageView.setImage(normalImage);
                break;
        }
    }

    public void updateBackgroundForLevel(int level) {
        String imagePath = String.format("/game/arkanoid/images/MapLevel%d.png", level);
        backgroundImageView.setImage(
                new javafx.scene.image.Image(getClass().getResource(imagePath).toExternalForm())
        );
    }

    @FXML
    private void startGame(ActionEvent event) {
        engine.setGameRunning(true);
        gameCanvas.requestFocus();
    }

    @FXML
    private void pauseGame(ActionEvent event) {
        engine.setGameRunning(!engine.isGameRunning());
        gameCanvas.requestFocus();
    }

    @FXML
    private void resetGame(ActionEvent event) {
        engine.startNewGame();
        gameCanvas.requestFocus();
    }

    @FXML
    private void returnToMenu(ActionEvent event) {
        engine.setGameRunning(false);
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml")
            );
            javafx.stage.Stage stage = (javafx.stage.Stage)
                    ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
