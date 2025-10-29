package game.arkanoid.controllers;

import game.arkanoid.views.GameEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private ImageView startImageView;
    @FXML
    private ImageView pauseImageView;
    @FXML
    private ImageView resetImageView;
    @FXML
    private ImageView menuImageView;
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

    private GameEngine engine;

    private Stage pauseStage;

    // Xử lý sự kiện khi di chuột vào button
    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        Image hoverImage = null;

        switch (buttonId) {
            case "startButton":
                hoverImage = new Image(
                        getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hoverImage);
                break;
            case "pauseButton":
                hoverImage = new Image(
                        getClass().getResource("/game/arkanoid/images/pause c.png").toExternalForm());
                pauseImageView.setImage(hoverImage);
                break;
            case "resetButton":
                hoverImage = new Image(
                        getClass().getResource("/game/arkanoid/images/reset c.png").toExternalForm());
                resetImageView.setImage(hoverImage);
                break;
            case "menuButton":
                hoverImage = new Image(
                        getClass().getResource("/game/arkanoid/images/MainMenu c.png").toExternalForm());
                menuImageView.setImage(hoverImage);
                break;
        }
    }

    // Xử lý sự kiện khi di chuột ra khỏi button
    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        Image normalImage = null;

        switch (buttonId) {
            case "startButton":
                normalImage = new Image(
                        getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normalImage);
                break;
            case "pauseButton":
                normalImage = new Image(
                        getClass().getResource("/game/arkanoid/images/pause.png").toExternalForm());
                pauseImageView.setImage(normalImage);
                break;
            case "resetButton":
                normalImage = new Image(
                        getClass().getResource("/game/arkanoid/images/reset.png").toExternalForm());
                resetImageView.setImage(normalImage);
                break;
            case "menuButton":
                normalImage = new Image(
                        getClass().getResource("/game/arkanoid/images/MainMenu.png").toExternalForm());
                menuImageView.setImage(normalImage);
                break;
        }
    }

    // Thay đổi ảnh nền theo level
    public void updateBackgroundForLevel(int level) {
        String imagePath = String.format("/game/arkanoid/images/MapLevel%d.png", level);
        backgroundImageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo GameEngine và liên kết với UI components
        engine = new GameEngine();
        engine.setMainController(this);
        engine.initializeGame(gameCanvas, scoreLabel, livesLabel, levelLabel);

        // Thiết lập ảnh nền cho level đầu tiên
        updateBackgroundForLevel(1);
        levelLabel.setText("Level: " + engine.getCurrentLevel());
        
        // Thiết lập event handlers khi scene được load
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Ràng buộc kích thước ảnh nền theo kích thước scene
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
                // Ràng buộc kích thước ảnh nền theo kích thước scene
                backgroundImageView.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImageView.fitHeightProperty().bind(newScene.heightProperty());

                // Ràng buộc kích thước canvas theo khu vực trung tâm có sẵn
                gameCanvas.widthProperty().bind(newScene.widthProperty());
                gameCanvas.heightProperty().bind(newScene.heightProperty().subtract(topBar.heightProperty())
                        .subtract(bottomBar.heightProperty()));
                // Khởi tạo game sau khi layout hoàn tất để canvas có kích thước thực
                javafx.application.Platform.runLater(() -> {
                    engine.startNewGame();
                    gameCanvas.requestFocus();
                });
            }
        });
    }

    // Bắt đầu
    @FXML
    private void startGame(ActionEvent event) {
        // Bắt đầu/bật lại vòng lặp game
        engine.setGameRunning(true);
        gameCanvas.requestFocus(); // trả lại focus cho canvas
    }

    // Tạm dừng
    @FXML
    private void pauseGame(ActionEvent event) {
        if (engine.isGameRunning()) {
            engine.setGameRunning(false);
            showPauseMenu();
        }
        gameCanvas.requestFocus();
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

            pauseStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resumeGame() {
        engine.setGameRunning(true);
        gameCanvas.requestFocus();
    }

    // Reload skin từ GameSettings
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

    // Reset game
    @FXML
    private void resetGame(ActionEvent event) {
        // Đặt lại trò chơi về trạng thái bắt đầu
        engine.resetCurrentLevel();
        gameCanvas.requestFocus(); // trả lại focus cho canvas
    }

    // Trở về menu chính
    @FXML
    private void returnToMenu(ActionEvent event) {
        // Dừng game engine
        engine.setGameRunning(false);
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(
                    "/game/arkanoid/fxml/StartMenu.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene()
                    .getWindow();
            stage.setScene(new javafx.scene.Scene(root, 800, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
