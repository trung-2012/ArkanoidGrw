package game.arkanoid.controllers;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import game.arkanoid.views.GameEngine;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import game.arkanoid.sound.SoundManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController implements Initializable {
    @FXML
    private Button startButton, pauseButton, resetButton, menuButton;
    @FXML
    private ImageView startImageView, pauseImageView, resetImageView, menuImageView;
    @FXML
    private Label scoreLabel, livesLabel, levelLabel;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private javafx.scene.layout.HBox topBar, bottomBar;
    @FXML
    private ImageView soundToggleImageView;

    private GameEngine engine;

    @FXML
    private void toggleGameSound() {
        boolean newState = !SoundManager.isSoundEnabled();
        SoundManager.setSoundEnabled(newState);

        if (newState) {
            soundToggleImageView.setImage(
                    new Image(getClass().getResource("/game/arkanoid/images/sound_on.png").toExternalForm())
            );
            SoundManager.playGameMusic();
            System.out.println("Sound ON (global)");
        } else {
            soundToggleImageView.setImage(
                    new Image(getClass().getResource("/game/arkanoid/images/sound_off.png").toExternalForm())
            );
            System.out.println("Sound OFF (global)");
        }
    }

    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent e) {
        Button btn = (Button) e.getSource();
        String id = btn.getId();
        Image hover = null;

        switch (id) {
            case "startButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hover);
                break;
            case "pauseButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/pause c.png").toExternalForm());
                pauseImageView.setImage(hover);
                break;
            case "resetButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/reset c.png").toExternalForm());
                resetImageView.setImage(hover);
                break;
            case "menuButton":
                hover = new Image(getClass().getResource("/game/arkanoid/images/MainMenu c.png").toExternalForm());
                menuImageView.setImage(hover);
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent e) {
        Button btn = (Button) e.getSource();
        String id = btn.getId();
        Image normal = null;

        switch (id) {
            case "startButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normal);
                break;
            case "pauseButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/pause.png").toExternalForm());
                pauseImageView.setImage(normal);
                break;
            case "resetButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/reset.png").toExternalForm());
                resetImageView.setImage(normal);
                break;
            case "menuButton":
                normal = new Image(getClass().getResource("/game/arkanoid/images/MainMenu.png").toExternalForm());
                menuImageView.setImage(normal);
                break;
        }
    }

    public void updateBackgroundForLevel(int level) {
        String path = String.format("/game/arkanoid/images/MapLevel%d.png", level);
        backgroundImageView.setImage(new Image(getClass().getResource(path).toExternalForm()));
    }

    @Override
    public void initialize(URL loc, ResourceBundle res) {
        if (SoundManager.isSoundEnabled()) {
            SoundManager.stopAll();
            SoundManager.playGameMusic();
            soundToggleImageView.setImage(
                    new Image(getClass().getResource("/game/arkanoid/images/sound_on.png").toExternalForm())
            );
        } else {
            soundToggleImageView.setImage(
                    new Image(getClass().getResource("/game/arkanoid/images/sound_off.png").toExternalForm())
            );
        }

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
                        case LEFT, A -> engine.setLeftPressed(true);
                        case RIGHT, D -> engine.setRightPressed(true);
                    }
                });
                newScene.setOnKeyReleased(e -> {
                    switch (e.getCode()) {
                        case LEFT, A -> engine.setLeftPressed(false);
                        case RIGHT, D -> engine.setRightPressed(false);
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
    private void startGame(ActionEvent e) {
        engine.setGameRunning(true);
        gameCanvas.requestFocus();
    }

    @FXML
    private void pauseGame(ActionEvent e) {
        engine.setGameRunning(!engine.isGameRunning());
        gameCanvas.requestFocus();
    }

    @FXML
    private void resetGame(ActionEvent e) {
        engine.startNewGame();
        gameCanvas.requestFocus();
    }

    @FXML
    private void returnToMenu(ActionEvent e) {
        engine.setGameRunning(false);
        SoundManager.stopAll();
        if (SoundManager.isSoundEnabled()) SoundManager.playMenuMusic();

        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
