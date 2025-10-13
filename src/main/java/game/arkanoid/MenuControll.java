package game.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class MenuControll {

    @FXML private Canvas starCanvas;
    @FXML private ImageView ballPreview;
    @FXML private ImageView paddlePreview;

    private double[] starsX, starsY;
    private int starCount = 200;
    private double speed = 0.5;

    // Các skin đang chọn (được cập nhật từ màn hình chọn skin)
    private static Image selectedBallImage;
    private static Image selectedPaddleImage;

    // Getter / Setter để truyền giữa scenes
    public static void setSelectedBallImage(Image img) { selectedBallImage = img; }
    public static Image getSelectedBallImage() { return selectedBallImage; }
    public static void setSelectedPaddleImage(Image img) { selectedPaddleImage = img; }
    public static Image getSelectedPaddleImage() { return selectedPaddleImage; }

    @FXML
    public void initialize() {
        // --- Hiệu ứng sao ---
        GraphicsContext gc = starCanvas.getGraphicsContext2D();
        Random rand = new Random();
        starsX = new double[starCount];
        starsY = new double[starCount];

        for (int i = 0; i < starCount; i++) {
            starsX[i] = rand.nextDouble() * starCanvas.getWidth();
            starsY[i] = rand.nextDouble() * starCanvas.getHeight();
        }

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, starCanvas.getWidth(), starCanvas.getHeight());
                gc.setFill(Color.WHITE);
                for (int i = 0; i < starCount; i++) {
                    gc.fillOval(starsX[i], starsY[i], 2, 2);
                    starsY[i] += speed;
                    if (starsY[i] > starCanvas.getHeight()) {
                        starsY[i] = 0;
                        starsX[i] = rand.nextDouble() * starCanvas.getWidth();
                    }
                }
            }
        }.start();

        // --- Hiển thị skin preview nếu đã chọn ---
        if (selectedBallImage != null) ballPreview.setImage(selectedBallImage);
        if (selectedPaddleImage != null) paddlePreview.setImage(selectedPaddleImage);
    }

    // --- Chuyển sang màn hình chọn skin ---
    @FXML
    private void onChooseSkin() {
        try {
            Stage stage = (Stage) starCanvas.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/SkinSelect.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Bắt đầu game ---
    @FXML
    private void onStartGame() {
        try {
            Stage stage = (Stage) starCanvas.getScene().getWindow();
            ArkanoidGame game = new ArkanoidGame();
            game.setBallImage(selectedBallImage);
            game.setPaddleImage(selectedPaddleImage);
            game.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Thoát game ---
    @FXML
    private void onExit() {
        System.exit(0);
    }
}
