package game.arkanoid.views;

import game.arkanoid.models.Ball;
import game.arkanoid.models.Paddle;
import game.arkanoid.models.Brick;
import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.LevelLoader;
import game.arkanoid.utils.GameSettings;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;
import java.util.ArrayList;

public class GameEngine extends AnimationTimer {
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    private int currentLevel = 1;
    private boolean gameRunning;
    private int lives = GameConstants.INITIAL_LIVES;
    private int score = 0;
    private Label scoreLabelRef;
    private Label livesLabelRef;
    private Label levelLabelRef;
    private game.arkanoid.controllers.MainController mainController;

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

    // Path skin mặc định
    private String ballSkinPath = "/game/arkanoid/images/Ball.png";
    private String paddleSkinPath = "/game/arkanoid/images/Paddle.png";

    @Override
    public void handle(long now) {
        if (!gameRunning) return;
        updateGameState();
        checkCollisions();
        render();
    }

    // Set skin từ Settings
    public void setBallSkin(String path) {
        this.ballSkinPath = path;
    }

    public void setPaddleSkin(String path) {
        this.paddleSkinPath = path;
    }

    public void initializeGame(Canvas canvas, Label scoreLabel, Label livesLabel, Label levelLabel) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        // Load ảnh Ball & Paddle theo skin đã chọn
        try {
            this.ballImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedPaddle()));
        } catch (Exception e) {
            System.out.println("Không thể load skin đã chọn, dùng mặc định.");
            this.ballImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Ball.png"));
            this.paddleImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Paddle.png"));
        }

        // Load ảnh gạch
        try {
            this.brickNormalImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickNormal.png"));
            this.brickWoodImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickWood.png"));
            this.brickIronImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickIron.png"));
            this.brickGoldImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickGold.png"));
            this.brickInsaneImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickInsane.png"));
        } catch (Exception e) {
            System.out.println("Không thể load ảnh gạch.");
        }

        // canvas focus để nhận phím
        try {
            this.canvas.requestFocus();
        } catch (Exception ignored) {
        }

        this.scoreLabelRef = scoreLabel;
        this.livesLabelRef = livesLabel;
        this.levelLabelRef = levelLabel;

        if (scoreLabelRef != null) scoreLabelRef.setText("Score: " + score);
        if (livesLabelRef != null) livesLabelRef.setText("Lives: " + lives);
        if (levelLabelRef != null) levelLabelRef.setText("Level: " + currentLevel);

        startNewGame();
    }

    // Bắt đầu game mới
    public void startNewGame() {
        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10;

        this.paddle = new Paddle(new Vector2D(px, py));

        double bx = px;
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0) - 150.0;
        this.ball = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        this.ball.setVelocity(new Vector2D(0.0, GameConstants.BALL_SPEED));

        loadLevelNumber(currentLevel);
        this.gameRunning = true;
        this.start();
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
            double resetX = paddle.getPosition().getX();
            double resetY = paddle.getPosition().getY() - (paddle.getHeight() / 2.0) - ball.getRadius() - 150.0;
            ball.setPosition(new Vector2D(resetX, resetY));
            // Bóng sẽ rơi thẳng xuống paddle (vận tốc chỉ có thành phần Y dương)
            ball.setVelocity(new Vector2D(0.0, GameConstants.BALL_SPEED));
            // giảm mạng và cập nhật label
            lives = Math.max(0, lives - 1);
            if (this.livesLabelRef != null) this.livesLabelRef.setText("Lives: " + lives);
            if (lives <= 0) {
                // Game over
                this.setGameRunning(false);
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                        Parent root = loader.load();
                        // truyền điểm vào controller
                        game.arkanoid.controllers.GameOverController controller = loader.getController();
                        controller.setFinalScore(score);
                        Stage stage = (Stage) canvas.getScene().getWindow();
                        stage.setScene(new Scene(root, 800, 600));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        // Kiểm tra va chạm với paddle
        ball.collideWith(paddle);

        // Kiểm tra va chạm với bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (ball.collideWith(brick)) {
                    // Sau khi va chạm, gạch chịu sát thương
                    // Chỉ cộng điểm khi gạch bị phá hủy
                    if (brick.isDestroyed()) {
                        switch (brick.getType()) {
                            case WOOD:
                                score += 20;
                                break;
                            case IRON:
                                score += 40;
                                break;
                            case GOLD:
                                score += 50;
                                break;
                            case INSANE:
                                score += 1000;
                                break;
                            case NORMAL:
                            default:
                                score += 10;
                                break;
                        }
                        if (this.scoreLabelRef != null) this.scoreLabelRef.setText("Score: " + score);
                    }
                    boolean anyLeft = false;
                    for (Brick b : bricks) {
                        if (!b.getDestroyed()) {
                            anyLeft = true;
                            break;
                        }
                    }
                    if (!anyLeft) {
                        // Hoàn thành level
                        currentLevel++;
                        if (currentLevel > GameConstants.totalLevels) {
                            // End game
                            this.setGameRunning(false);
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                                    Parent root = loader.load();
                                    game.arkanoid.controllers.GameOverController controller = loader.getController();
                                    controller.setFinalScore(score);
                                    Stage stage = (Stage) canvas.getScene().getWindow();
                                    stage.setScene(new Scene(root, 800, 600));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            // Load level tiếp theo
                            loadLevelNumber(currentLevel);
                            // Đặt lại vị trí bóng trên paddle và cho bóng tự rơi
                            double resetX2 = paddle.getPosition().getX();
                            double resetY2 = paddle.getPosition().getY() - (paddle.getHeight() / 2.0) - ball.getRadius()
                                    - 5.0;
                            ball.setPosition(new Vector2D(resetX2, resetY2));
                            ball.setVelocity(new Vector2D(0.0, GameConstants.BALL_SPEED));
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
        // Cập nhật ảnh nền khi chuyển level
        if (mainController != null) {
            Platform.runLater(() -> mainController.updateBackgroundForLevel(level));
        }
    }

    public void setMainController(game.arkanoid.controllers.MainController controller) {
        this.mainController = controller;
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
