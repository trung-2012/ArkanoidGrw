package game.arkanoid.views;

import game.arkanoid.models.Ball;
import game.arkanoid.models.Paddle;
import game.arkanoid.models.Brick;
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
    private int currentLevel = 1;
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
    // Vòng lặp game chính
    public void handle(long now) {
        if (!gameRunning)
            return;
        updateGameState();
        checkCollisions();
        render();
    }

    // Khởi tạo game engine với canvas để vẽ
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
        // Đảm bảo canvas có tiêu điểm để nhận sự kiện bàn phím
        try {
            this.canvas.requestFocus();
        } catch (Exception ignored) {
            System.out.println("Canvas not ready for focus request :((");
        }
        startNewGame();
    }

    // Bắt đầu game mới
    public void startNewGame() {
        // Tạo paddle ở chính giữa phía dưới màn hình
        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10;

        this.paddle = new Paddle(new Vector2D(px, py));

        // Tạo bóng ngay phía trên paddle
        double bx = px;
        double by = py - GameConstants.PADDLE_HEIGHT / 2.0 - GameConstants.BALL_SIZE;
        this.ball = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);

        // Tạo level đầu tiên
        loadLevelNumber(currentLevel);
        this.gameRunning = true;
        this.start(); // Khởi động AnimationTimer
    }

    // Kiểm tra va chạm
    public void checkCollisions() {
        if (ball == null)
            return;

        // Kiểm tra va chạm với tường
        double w = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double h = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        boolean out = ball.collideWithWall(w, h);
        if (out) {
            // Bóng rơi xuống đáy thì bay màu 1 mạng
            // Đặt lại vị trí bóng trên paddle
            ball.setPosition(new Vector2D(paddle.getPosition().getX(), paddle.getPosition().getY() - 30));
            ball.setVelocity(new Vector2D(GameConstants.BALL_SPEED, -GameConstants.BALL_SPEED));
        }

        // Kiểm tra va chạm với paddle
        ball.collideWith(paddle);

        // Kiểm tra va chạm với bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (ball.collideWith(brick)) {
                    // Sau khi va chạm, gạch chịu sát thương
                    boolean anyLeft = false;
                    for (Brick b : bricks) {
                        if (!b.getDestroyed()) { anyLeft = true; break; }
                    }
                    if (!anyLeft) {
                        // Hoàn thành level
                        currentLevel++;
                        if (currentLevel > GameConstants.totalLevels) {
                            // End game
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
                            // Load level tiếp theo
                            loadLevelNumber(currentLevel);
                            // Đặt lại vị trí bóng trên paddle
                            ball.setPosition(new Vector2D(paddle.getPosition().getX(), paddle.getPosition().getY() - 30));
                            ball.setVelocity(new Vector2D(GameConstants.BALL_SPEED, -GameConstants.BALL_SPEED));
                        }
                    }
                    break; // Chỉ xử lý một va chạm mỗi frame
                }
            }
        }

    }

    // Cập nhật trạng thái game
    public void updateGameState() {
        // Cập nhật vị trí paddle dựa trên trạng thái bấm phím
        if (paddle != null) {
            if (leftPressed && !rightPressed)
                paddle.moveLeft();
            if (rightPressed && !leftPressed)
                paddle.moveRight();
            // Đảm bảo paddle không đi ra ngoài màn hình
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

    // Xử lý sự kiện phím bấm
    public void setLeftPressed(boolean pressed) {
        this.leftPressed = pressed;
    }

    // Xử lý sự kiện phím bấm
    public void setRightPressed(boolean pressed) {
        this.rightPressed = pressed;
    }

    // Vẽ lại khung cảnh game
    public void render() {
        if (gc == null)
            return;
        // clear canvas 
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // vẽ bricks
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

        // vẽ paddle
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

        // vẽ ball
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

    // Load level
    public void loadLevelNumber(int level) {
        String file = "level" + level + ".txt";
        this.bricks = LevelLoader.loadLevel(file);
    }
    
    // Kiểm tra trạng thái game
    public boolean isGameRunning() {
        return gameRunning;
    }

    // Getters & Setters

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
