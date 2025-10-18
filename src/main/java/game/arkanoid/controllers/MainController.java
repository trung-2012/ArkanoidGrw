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
        // Khởi tạo engine, cấu hình canvas và ràng buộc kích thước/ sự kiện bàn phím
        engine = new GameEngine();
        engine.initializeGame(gameCanvas);
        // Hiển thị level hiện tại
        levelLabel.setText("Level: " + engine.getCurrentLevel());
        // Gắn sự kiện bàn phím khi scene sẵn sàng
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
                gameCanvas.heightProperty().bind(newScene.heightProperty().subtract(topBar.heightProperty()).subtract(bottomBar.heightProperty()));
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
        // Chuyển trạng thái tạm dừng/tiếp tục
        engine.setGameRunning(!engine.isGameRunning());
        gameCanvas.requestFocus(); // trả lại focus cho canvas
    }

    // Reset game
    @FXML
    private void resetGame(ActionEvent event) {
        // Đặt lại trò chơi về trạng thái bắt đầu
        engine.startNewGame();
        gameCanvas.requestFocus(); // trả lại focus cho canvas
    }

    // Trở về menu chính
    @FXML
    private void returnToMenu(ActionEvent event) {
        // Dừng game engine
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
