package game.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;

public class MenuControll {
    @FXML private Canvas starCanvas;
    @FXML private ComboBox<String> ballSkinBox;
    @FXML private ComboBox<String> paddleSkinBox;
    @FXML private ImageView ballPreview;
    @FXML private ImageView paddlePreview;

    private double[] starsX, starsY;
    private int starCount = 200;
    private double speed = 0.5;

    private Image selectedBallImage;
    private Image selectedPaddleImage;

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

        // --- ComboBox setup ---
        ballSkinBox.getItems().addAll("Red", "Fire", "Neon");
        paddleSkinBox.getItems().addAll("Blue", "Gold", "Wood");

        // Sự kiện chọn ball
        ballSkinBox.setOnAction(e -> {
            String val = ballSkinBox.getValue();
            String path = switch (val) {
                case "Fire" -> "/game/arkanoid/images/balls/ball.png";
                case "Neon" -> "/game/arkanoid/images/balls/ball.png";
                default -> "/game/arkanoid/images/balls/ball.png";
            };
            selectedBallImage = new Image(getClass().getResourceAsStream(path));
            ballPreview.setImage(selectedBallImage);
        });

        // Sự kiện chọn paddle
        paddleSkinBox.setOnAction(e -> {
            String val = paddleSkinBox.getValue();
            String path = switch (val) {
                case "Gold" -> "/game/arkanoid/images/paddles/paddle.png";
                case "Wood" -> "/game/arkanoid/images/paddles/paddle.png";
                default -> "/game/arkanoid/images/paddles/paddle.png";
            };
            selectedPaddleImage = new Image(getClass().getResourceAsStream(path));
            paddlePreview.setImage(selectedPaddleImage);
        });
    }

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

    @FXML
    private void onExit() {
        System.exit(0);
    }
}
