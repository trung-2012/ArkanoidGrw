package game.arkanoid;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ArkanoidGame extends Application {
    // Paddle
    double paddleX = 300, paddleY = 555, paddleW = 100, paddleH = 15;
    double paddleSpeed = 6;
    boolean moveLeft = false, moveRight = false;

    // Ball
    double ballX = 350, ballY = 300, ballR = 10;
    double dx = 3, dy = 3;

    // Bricks
    boolean[][] bricks;
    int brickRows = 5, brickCols = 10;
    double brickW = 60, brickH = 20;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        bricks = new boolean[brickRows][brickCols];

        // Init bricks
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                bricks[i][j] = true;
            }
        }

        Scene scene = new Scene(new javafx.scene.Group(canvas));
        stage.setScene(scene);
        stage.setTitle("Arkanoid JavaFX");
        stage.show();

        // Control
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) moveLeft = true;
            if (e.getCode() == KeyCode.RIGHT) moveRight = true;
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) moveLeft = false;
            if (e.getCode() == KeyCode.RIGHT) moveRight = false;
        });

        // Game loop
        new AnimationTimer() {
            public void handle(long now) {
                update();
                draw(gc);
            }
        }.start();
    }

    void update() {
        // Move paddle
        if (moveLeft) paddleX -= paddleSpeed;
        if (moveRight) paddleX += paddleSpeed;
        if (paddleX < 0) paddleX = 0;
        if (paddleX + paddleW > 800) paddleX = 800 - paddleW;

        // Move ball
        ballX += dx;
        ballY += dy;

        // Bounce walls
        if (ballX <= 0 || ballX >= 800 - ballR) dx *= -1;
        if (ballY <= 0) dy *= -1;

        // Bounce paddle
        if (ballY + ballR >= paddleY && ballX >= paddleX && ballX <= paddleX + paddleW) {
            dy *= -1;
            ballY = paddleY - ballR; // tránh kẹt
        }

        // Bounce bricks
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j]) {
                    double bx = j * brickW + 50;
                    double by = i * brickH + 50;
                    if (ballX + ballR > bx && ballX < bx + brickW &&
                            ballY + ballR > by && ballY < by + brickH) {
                        dy *= -1;
                        bricks[i][j] = false;
                    }
                }
            }
        }

        // Lose condition
        if (ballY > 600) {
            ballX = 350; ballY = 300;
            dx = 3; dy = 3;
        }
    }

    void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // Draw paddle
        gc.setFill(Color.BLUE);
        gc.fillRect(paddleX, paddleY, paddleW, paddleH);

        // Draw ball
        gc.setFill(Color.RED);
        gc.fillOval(ballX, ballY, ballR, ballR);

        // Draw bricks
        gc.setFill(Color.GREEN);
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j]) {
                    gc.fillRect(j * brickW + 50, i * brickH + 50, brickW - 2, brickH - 2);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
