package game.arkanoid.views;

import game.arkanoid.models.*;
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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends AnimationTimer {
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    private int currentLevel = 1;
    private boolean gameRunning;
    private int lives = GameConstants.INITIAL_LIVES;
    private int score = 0;
    private int totalScore = 0;
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final Random random = new Random();
    private boolean laserActive = false;
    private final List<LaserBeam> laserBeams = new ArrayList<>();
    private Shield shield;


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

    @Override
    public void handle(long now) {
        if (!gameRunning)
            return;
        updateGameState();
        updatePowerUps();
        checkCollisions();
        render();
    }

    // Reload skin từ GameSettings (gọi khi user thay đổi trong Settings)
    public void reloadSkins() {
        try {
            this.ballImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedPaddle()));
            System.out.println("Đã reload skins: Ball=" + GameSettings.getSelectedBall() + ", Paddle=" + GameSettings.getSelectedPaddle());
        } catch (Exception e) {
            System.err.println("Lỗi khi reload skins: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeGame(Canvas canvas, Label scoreLabel, Label livesLabel, Label levelLabel) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        // Load ảnh Ball & Paddle theo skin đã chọn từ GameSettings
        try {
            this.ballImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedPaddle()));
        } catch (Exception e) {
            System.err.println("Không thể load skin đã chọn, dùng mặc định: " + e.getMessage());
            // Fallback về skin mặc định
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

        if (scoreLabelRef != null)
            scoreLabelRef.setText("Score: " + totalScore);
        if (livesLabelRef != null)
            livesLabelRef.setText("Lives: " + lives);
        if (levelLabelRef != null)
            levelLabelRef.setText("Level: " + currentLevel);

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

    // Reset lại màn chơi hiện tại
    public void resetCurrentLevel() {
        this.score = this.totalScore;
        if (this.scoreLabelRef != null) {
            this.scoreLabelRef.setText("Score: " + totalScore);
        }

        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10;

        this.paddle = new Paddle(new Vector2D(px, py));
        double bx = px;
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0) - 150.0;
        this.ball = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        this.ball.setVelocity(new Vector2D(0.0, GameConstants.BALL_SPEED));

        this.loadLevelNumber(currentLevel);
        this.gameRunning = true;
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
            if (this.livesLabelRef != null)
                this.livesLabelRef.setText("Lives: " + lives);
            if (lives <= 0) {
                // Game over
                this.totalScore = this.score;
                this.setGameRunning(false);
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                        Parent root = loader.load();
                        // truyền điểm vào controller
                        game.arkanoid.controllers.GameOverController controller = loader.getController();
                        controller.setFinalScore(totalScore);
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

        // Kiểm tra va chạm với shield
        if (shield != null && shield.collidesWith(ball)) {
            ball.reverseVelocityY();
            shield.hit();
            if (shield.isBroken()) {
                shield = null;
            }
        }

        // Kiểm tra va chạm với bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (ball.collideWith(brick)) {
                    boolean anyLeft = false;
                    for (Brick b : bricks) {
                        if (!b.getDestroyed()) {
                            anyLeft = true;
                            break;
                        }
                    }
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
                        // 20% rơi power-up
                        //nếu còn 1 viên gạch thì sẽ không rơi powerUp
                        if (random.nextDouble() < GameConstants.POWER_UP_RATE && anyLeft) {
                            PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];
                            PowerUp powerUp = new PowerUp(brick.getPosition().getX() + GameConstants.BRICK_WIDTH / 2, brick.getPosition().getY(), type);
                            powerUps.add(powerUp);
                        }
                        if (this.scoreLabelRef != null)
                            this.scoreLabelRef.setText("Score: " + score);
                    }
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
                            this.totalScore = this.score;
                            this.setGameRunning(false);
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(
                                            getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                                    Parent root = loader.load();
                                    game.arkanoid.controllers.GameOverController controller = loader.getController();
                                    controller.setFinalScore(totalScore);
                                    Stage stage = (Stage) canvas.getScene().getWindow();
                                    stage.setScene(new Scene(root, 800, 600));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            // Cập nhật tổng điểm hiện tại
                            this.totalScore = this.score;
                            // Load level tiếp theo
                            loadLevelNumber(currentLevel);
                            // Đặt lại vị trí bóng trên paddle và cho bóng tự rơi
                            double resetX2 = paddle.getPosition().getX();
                            double resetY2 = paddle.getPosition().getY()
                                    - (paddle.getHeight() / 2.0) - ball.getRadius() - 150.0;
                            ball.setPosition(new Vector2D(resetX2, resetY2));
                            ball.setVelocity(new Vector2D(0.0, GameConstants.BALL_SPEED));
                        }
                    }
                    break; // Chỉ xử lý một va chạm mỗi frame
                }
            }
        }

    }

    private void updatePowerUps() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update();

            // Kiểm tra va chạm với paddle
            if (powerUp.intersects(paddle)) {
                activatePowerUp(powerUp);
                iterator.remove();
                continue;
            }

            // Nếu rơi ra ngoài màn hình
            if (powerUp.getY() > canvas.getHeight()) {
                iterator.remove();
            }
        }
    }

    private void activatePowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case LASER:
                // Kích hoạt laser
                activateLaser();
                break;

            case EXTRA_LIFE:
                // Thêm 1 mạng, tối đa 5 mạng
                if (lives < GameConstants.MAX_LIVE) {
                    lives++;
                }
                if (livesLabelRef != null) {
                    livesLabelRef.setText("Lives: " + lives);
                }
                break;
            case SHIELD:
                shield = new Shield(0, canvas.getHeight()-GameConstants.SHIELD_HEIGHT, canvas.getWidth(), GameConstants.SHIELD_HEIGHT);
        }
    }

    private void activateLaser() {
        if (laserActive) return; // tránh kích hoạt nhiều lần
        laserActive = true;

        new Thread(() -> {
            try {
                int shots = GameConstants.NUM_OF_BULLETS;
                for (int i = 0; i < shots; i++) {
                    double px = paddle.getPosition().getX();
                    double py = paddle.getPosition().getY() - paddle.getHeight() / 2;
                    // tạo hai tia laser hai bên paddle
                    double offset = paddle.getWidth() / 2 - 10;
                    addLaser(px - offset, py);
                    addLaser(px + offset, py);

                    Thread.sleep(GameConstants.COOL_DOWN_TIME); // thời gian giữa mỗi lần bắn
                }
            } catch (InterruptedException ignored) {
            } finally {
                laserActive = false;
            }
        }).start();
    }

    private void addLaser(double x, double y) {
        Platform.runLater(() -> laserBeams.add(new LaserBeam(new Vector2D(x, y))));
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

        // Cập nhật power-ups đang rơi
        Iterator<PowerUp> iter = powerUps.iterator();
        while (iter.hasNext()) {
            PowerUp p = iter.next();
            p.update(); // rơi xuống
            // Nếu power-up rơi chạm paddle
            if (paddle != null && p.intersects(paddle)) {
                activatePowerUp(p); // kích hoạt hiệu ứng
                iter.remove();    // xóa power-up khỏi danh sách
            } else if (p.getY() > canvas.getHeight()) {
                iter.remove();    // xóa nếu rơi khỏi màn hình
            }
        }

        // Cập nhật các tia laser
        Iterator<LaserBeam> laserIter = laserBeams.iterator();
        while (laserIter.hasNext()) {
            LaserBeam beam = laserIter.next();
            beam.update();

            // Xóa nếu ra khỏi màn hình hoặc va chạm với gạch
            boolean hit = false;
            for (Brick brick : bricks) {
                if (!brick.isDestroyed()) {
                    double bx = brick.getPosition().getX();
                    double by = brick.getPosition().getY();

                    if (beam.getPosition().getX() >= bx &&
                            beam.getPosition().getX() <= bx + GameConstants.BRICK_WIDTH &&
                            beam.getPosition().getY() >= by &&
                            beam.getPosition().getY() <= by + GameConstants.BRICK_HEIGHT) {

                        brick.takeDamage();
                        if (brick.isDestroyed()) {
                            score += 10;
                            Platform.runLater(() -> scoreLabelRef.setText("Score: " + score));
                        }

                        // Xóa laser ngay khi trúng gạch
                        hit = true;
                        break;
                    }
                }
            }

            if (hit || beam.isOffScreen(canvas.getHeight())) {
                laserIter.remove();
            }
        }
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
        //vẽ power up
        for (PowerUp p : powerUps) {
            Image img = p.getImage();
            if (img != null) {
                gc.drawImage(img, p.getX() - p.getSize() / 2, p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
            } else {
                gc.setFill(Color.LIMEGREEN);
                gc.fillOval(p.getX() - p.getSize() / 2, p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
            }
        }
        //vẽ laserBeam
        for (LaserBeam beam : laserBeams) {
            beam.render(gc);
        }
        //vẽ shield
        if (shield != null) {
            shield.draw(gc);
        }
    }

    // Load level
    public void loadLevelNumber(int level) {
        levelLabelRef.setText("Level: " + level);
        this.lives = GameConstants.INITIAL_LIVES;
        livesLabelRef.setText("Lives: " + this.lives);
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
