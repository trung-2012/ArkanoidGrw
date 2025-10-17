package game.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class MenuControll{
    @FXML
    private Canvas starCanvas;

    private double[] starsX, starsY;
    private int starCount = 200; // số lượng sao
    private double speed = 0.5;  // tốc độ cuộn

    @FXML
    public void initialize() {
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

                    // reset lại nếu sao rơi xuống ngoài màn
                    if (starsY[i] > starCanvas.getHeight()) {
                        starsY[i] = 0;
                        starsX[i] = rand.nextDouble() * starCanvas.getWidth();
                    }
                }
            }
        }.start();
    }

    @FXML
    private void onStartGame() {
        System.out.println("Start Game clicked!");
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }
}
