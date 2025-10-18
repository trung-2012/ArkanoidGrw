package game.arkanoid.views;

import game.arkanoid.models.Ball;
import game.arkanoid.models.Paddle;
import game.arkanoid.models.Brick;
import game.arkanoid.models.BrickType;
import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.LevelLoader;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;
import java.util.ArrayList;

public class GameEngine extends AnimationTimer {
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    private int currentLevel = 1; // level hiện tại
    private boolean gameRunning;

    private Canvas canvas;
    private GraphicsContext gc;
    private Image paddleImage;
    private Image ballImage;
    private Image brickNormalImage;
    private Image brickWoodImage;
    private Image brickIronImage;
    private Image brickGoldImage;
    private Image brickInsaneImage;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    @Override
    public void handle(long now) {
        if (!gameRunning)
            return;
        updateGameState();
        checkCollisions();
        render();
    }

    public void initializeGame(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        try {
            this.paddleImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/Paddle.png"));
            this.ballImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/Ball.png"));
            this.brickNormalImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/BrickNormal.png"));
            this.brickWoodImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/BrickWood.png"));
            this.brickIronImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/BrickIron.png"));
            this.brickGoldImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/BrickGold.png"));
            this.brickInsaneImage = new Image(getClass().getResourceAsStream(
                    "/game/arkanoid/images/BrickInsane.png"));
        } catch (Exception e) {
            this.paddleImage = null;
        }
        // ensure canvas has focus for key events
        try {
            this.canvas.requestFocus();
        } catch (Exception ignored) {
        }
        startNewGame();
    }

    public void startNewGame() {
        // create paddle centered at bottom using canvas dimensions when available
        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10; // 10px margin from bottom

        this.paddle = new Paddle(new Vector2D(px, py));

        // create ball above paddle
        double bx = px;
        double by = py - GameConstants.PADDLE_HEIGHT / 2.0 - GameConstants.BALL_SIZE;
        this.ball = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);

        // create bricks grid
        loadLevelNumber(currentLevel);
        this.gameRunning = true;
        this.start(); // start AnimationTimer
    }

    public void checkCollisions() {
        if (ball == null)
            return;

        // Wall collisions (use canvas size when available)
        double w = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double h = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        boolean out = ball.collideWithWall(w, h);
        if (out) {
            // for now, restart ball above paddle
            // reduce lives behavior could be added here; currently reset ball to paddle
            ball.setPosition(new Vector2D(paddle.getPosition().getX(), paddle.getPosition().getY() - 30));
            ball.setVelocity(new Vector2D(GameConstants.BALL_SPEED, -GameConstants.BALL_SPEED));
        }

        // Paddle collision
        ball.collideWith(paddle);

        // Brick collisions
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (ball.collideWith(brick)) {
                    // After hit, check if all destroyed
                    boolean anyLeft = false;
                    for (Brick b : bricks) {
                        if (!b.getDestroyed()) { anyLeft = true; break; }
                    }
                    if (!anyLeft) {
                        // Level cleared
                        currentLevel++;
                        if (currentLevel > GameConstants.totalLevels) {
                            // completed all levels -> show win screen (simple fallback here)
                            this.setGameRunning(false);
                            Platform.runLater(() -> {
                                try {
                                    Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                                    Stage stage = (Stage) canvas.getScene().getWindow();
                                    stage.setScene(new Scene(root, 800, 600));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            // load next level: reset ball/paddle and bricks
                            loadLevelNumber(currentLevel);
                            // put ball above paddle
                            ball.setPosition(new Vector2D(paddle.getPosition().getX(), paddle.getPosition().getY() - 30));
                            ball.setVelocity(new Vector2D(GameConstants.BALL_SPEED, -GameConstants.BALL_SPEED));
                        }
                    }
                    break; // handle one collision per frame
                }
            }
        }

    }

    public void updateGameState() {
        // move paddle if input flags are set
        if (paddle != null) {
            if (leftPressed && !rightPressed)
                paddle.moveLeft();
            if (rightPressed && !leftPressed)
                paddle.moveRight();
            // clamp paddle inside canvas
            double half = paddle.getWidth() / 2.0;
            double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
            if (paddle.getPosition().getX() - half < 0)
                paddle.getPosition().setX(half);
            if (paddle.getPosition().getX() + half > canvasW)
                paddle.getPosition().setX(canvasW - half);
        }
        if (ball != null)
            ball.update();
    }

    public void setLeftPressed(boolean pressed) {
        this.leftPressed = pressed;
    }

    public void setRightPressed(boolean pressed) {
        this.rightPressed = pressed;
    }

    public void render() {
        if (gc == null)
            return;
        // clear canvas (leave transparent so underlying ImageView background can show through)
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // draw bricks (image if available)
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                double bx = brick.getPosition().getX();
                double by = brick.getPosition().getY();
                Image img = null;
                switch (brick.getType()) {
                    case WOOD:
                        img = brickWoodImage;
                        break;
                    case IRON:
                        img = brickIronImage;
                        break;
                    case GOLD:
                        img = brickGoldImage;
                        break;
                    case INSANE:
                        img = brickInsaneImage;
                        break;
                    case NORMAL:
                    default:
                        img = brickNormalImage;
                        break;
                }
                if (img != null) {
                    gc.drawImage(img, bx, by, GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT);
                } else {
                    gc.setFill(Color.DARKORANGE);
                    gc.fillRect(bx, by, GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT);
                }
            }
        }

        // draw paddle
        double pw = paddle.getWidth();
        double ph = paddle.getHeight();
        double px = paddle.getPosition().getX() - pw / 2.0;
        double py = paddle.getPosition().getY() - ph / 2.0;
        if (paddleImage != null) {
            gc.drawImage(paddleImage, px, py, pw, ph);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(px, py, pw, ph);
        }

        // draw ball (image if available)
        double bx = ball.getPosition().getX() - ball.getRadius();
        double by = ball.getPosition().getY() - ball.getRadius();
        double size = ball.getRadius() * 2;
        if (ballImage != null) {
            gc.drawImage(ballImage, bx, by, size, size);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(bx, by, size, size);
        }
    }

    public void loadLevelNumber(int level) {
        String file = "level" + level + ".txt";
        this.bricks = LevelLoader.loadLevel(file);
    }

    // Getters và Setters

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
