package game.arkanoid.views;

import game.arkanoid.managers.CollisionManager;
import game.arkanoid.managers.PowerUpManager;
import game.arkanoid.managers.RenderManager;
import game.arkanoid.models.*;
import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.GameSettings;
import game.arkanoid.utils.LevelLoader;
import game.arkanoid.utils.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEngine extends AnimationTimer {
    // MANAGERS
    private CollisionManager collisionManager;
    private RenderManager renderManager;
    private PowerUpManager powerUpManager;

    // GAME OBJECTS
    // Thread-safe collections for concurrent access
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final List<LaserBeam> laserBeams = new CopyOnWriteArrayList<>();
    private final List<ExplosionEffect> explosions = new CopyOnWriteArrayList<>();
    
    private final Random random = new Random();
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    
    // GAME STATE
    private int currentLevel = 1;
    private boolean gameRunning;
    private int lives = GameConstants.INITIAL_LIVES;
    private int score = 0;
    private int totalScore = 0;
    
    private Shield shield;

    // UI REFERENCES
    private Label scoreLabelRef;
    private Label livesLabelRef;
    private Label levelLabelRef;
    private game.arkanoid.controllers.MainController mainController;

    private Canvas canvas;

    // IMAGES
    private Image paddleImage;
    private Image ballImage;
    private Image bulletImage;
    private Image explosionEffectImage;

    // INPUT STATE
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean ballAttachedToPaddle = true;
    private double chargePulse = 0;
    private boolean chargeIncreasing = true;

    @Override
    public void handle(long now) {
        if (!gameRunning)
            return;
        updateGameState();
        powerUpManager.updatePowerUps();
        powerUpManager.updateLaserBeams(collisionManager);
        checkCollisions();
        render();
    }

    // Reload skin từ GameSettings (gọi khi user thay đổi trong Settings)
    public void reloadSkins() {
        try {
            this.ballImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedPaddle()));
            this.bulletImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBullet()));
            
            // Update images cho RenderManager
            if (renderManager != null) {
                renderManager.setBallImage(ballImage);
                renderManager.setPaddleImage(paddleImage);
            }
            
            System.out.println("Đã reload skins: Ball=" + GameSettings.getSelectedBall() + ", Paddle="
                    + GameSettings.getSelectedPaddle() + ", Bullet=" + GameSettings.getSelectedBullet());
        } catch (Exception e) {
            System.err.println("Lỗi khi reload skins: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeGame(Canvas canvas, Label scoreLabel, Label livesLabel, Label levelLabel) {
        this.canvas = canvas;
        
        // Khởi tạo RenderManager
        this.renderManager = new RenderManager(canvas);
        
        // Khởi tạo PowerUpManager
        this.powerUpManager = new PowerUpManager(powerUps, laserBeams);
        this.powerUpManager.setPaddle(paddle);
        this.powerUpManager.setCanvas(canvas);

        // Load ảnh Ball & Paddle theo skin đã chọn từ GameSettings
        try {
            this.ballImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedPaddle()));
            this.bulletImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBullet()));
            
            // Set images cho managers
            renderManager.setBallImage(ballImage);
            renderManager.setPaddleImage(paddleImage);
            powerUpManager.setBulletImage(bulletImage);
        } catch (Exception e) {
            System.err.println("Không thể load skin đã chọn, dùng mặc định: " + e.getMessage());
            // Fallback về skin mặc định
            this.ballImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Ball.png"));
            this.paddleImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Paddle.png"));
            this.bulletImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/bulletPaddle.png"));
            
            renderManager.setBallImage(ballImage);
            renderManager.setPaddleImage(paddleImage);
            powerUpManager.setBulletImage(bulletImage);
        }

        try {
            this.explosionEffectImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/ExplodeEffect.png"));
        } catch (Exception e) {
            System.out.println("Không thể load ảnh explosion effect.");
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
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0);
        this.ball = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        this.ball.setVelocity(new Vector2D(0.0, 0.0));
        this.ballAttachedToPaddle = true;

        loadLevelNumber(currentLevel);
        
        // Khởi tạo CollisionManager và setup callbacks
        this.collisionManager = new CollisionManager(ball, paddle, bricks, canvas);
        setupCollisionCallbacks();
        
        // Setup PowerUpManager
        powerUpManager.setPaddle(paddle);
        setupPowerUpCallbacks();
        
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
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0);
        this.ball = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        this.ball.setVelocity(new Vector2D(0.0, 0.0));
        this.ballAttachedToPaddle = true;

        // Khởi tạo CollisionManager
        this.collisionManager = new CollisionManager(ball, paddle, bricks, canvas);
        setupCollisionCallbacks();

        // Clear power ups và lasers
        powerUpManager.clearPowerUps();
        powerUpManager.clearLaserBeams();
        powerUpManager.setPaddle(paddle);
        setupPowerUpCallbacks();

        // Xóa shield nếu có
        shield = null;

        this.loadLevelNumber(currentLevel);
        this.gameRunning = true;
    }

    // Kiểm tra va chạm - Delegate cho CollisionManager
    public void checkCollisions() {
        if (ball == null || collisionManager == null)
            return;

        // Update collision manager với game state hiện tại
        collisionManager.setBricks(bricks);
        collisionManager.setShield(shield);
        
        // Delegate collision detection cho CollisionManager
        // Tất cả logic xử lý va chạm (cộng điểm, spawn power-up, level complete)
        // được handle thông qua callbacks
        collisionManager.checkAllCollisions();
    }

    // Xử lý nổ cho ExplodeBrick
    private void handleExplosion(ExplodeBrick explodedBrick) {
        // Tạo hiệu ứng nổ cho gạch bị phá
        explosions.add(new ExplosionEffect(explodedBrick.getPosition(), explosionEffectImage));
        
        // Random 50-50: nổ theo hàng (true) hoặc theo cột (false)
        boolean explodeRow = random.nextBoolean();
        
        double explodedX = explodedBrick.getPosition().getX();
        double explodedY = explodedBrick.getPosition().getY();
        
        for (Brick brick : bricks) {
            if (brick == explodedBrick || brick.isDestroyed()) continue;
            
            double bx = brick.getPosition().getX();
            double by = brick.getPosition().getY();
            
            boolean shouldExplode = false;
            
            if (explodeRow) {
                // Nổ theo hàng (cùng tọa độ Y)
                if (Math.abs(by - explodedY) < 5) {
                    shouldExplode = true;
                }
            } else {
                // Nổ theo cột (cùng tọa độ X)
                if (Math.abs(bx - explodedX) < 5) {
                    shouldExplode = true;
                }
            }
            
            if (shouldExplode) {
                brick.setDestroyed(true);
                score += brick.getPoints();
                explosions.add(new ExplosionEffect(brick.getPosition(), explosionEffectImage));
                
                if (brick instanceof ExplodeBrick) {
                    handleExplosion((ExplodeBrick) brick);
                }
            }
        }
        
        if (scoreLabelRef != null) {
            scoreLabelRef.setText("Score: " + score);
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
        if (ball != null) {
            if (ballAttachedToPaddle) {
                ball.getTrail().clear();
                // charge aura
                if (chargeIncreasing) {
                    chargePulse += 0.01;
                    if (chargePulse > 1.0) chargeIncreasing = false;
                } else {
                    chargePulse -= 0.01;
                    if (chargePulse < 0.0) chargeIncreasing = true;
                }
                // Giữ bóng trên paddle
                double bx = paddle.getPosition().getX();
                double by = paddle.getPosition().getY() - (paddle.getHeight() / 2.0) - ball.getRadius();
                ball.setPosition(new Vector2D(bx, by));
            } else {
                ball.update();
            }
        }
        
        // Power-ups và lasers được handle bởi PowerUpManager
        
        // Power-ups và lasers được handle bởi PowerUpManager
        
        // Cập nhật hiệu ứng nổ và xóa các explosion đã kết thúc
        for (ExplosionEffect explosion : explosions) {
            explosion.update();
        }
        
        // Xóa các explosion đã kết thúc bằng removeIf()
        explosions.removeIf(ExplosionEffect::isFinished);
    }

    private void handleLevelCompletion() {
        // Xóa tất cả power-ups đang rơi
        powerUps.clear();

        // Xóa tất cả laser beams
        laserBeams.clear();

        // Xóa shield nếu có
        shield = null;

        currentLevel++;
        if (currentLevel > GameConstants.totalLevels) {
            totalScore = score;
            setGameRunning(false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                Parent root = loader.load();
                game.arkanoid.controllers.GameOverController controller = loader.getController();
                controller.setFinalScore(totalScore);
                Stage stage = (Stage) canvas.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            totalScore = score;
            loadLevelNumber(currentLevel);
            double resetX = paddle.getPosition().getX();
            double resetY = paddle.getPosition().getY() - (paddle.getHeight() / 2.0) - ball.getRadius();
            ball.setPosition(new Vector2D(resetX, resetY));
            ball.setVelocity(new Vector2D(0.0, 0.0));
            ballAttachedToPaddle = true;
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

    // Render game - Delegate cho RenderManager
    public void render() {
        if (renderManager == null) return;
        
        // Update charge pulse effect
        renderManager.setChargePulse(chargePulse);
        
        // Delegate rendering cho RenderManager
        renderManager.renderAll(
            ball,
            paddle,
            bricks,
            powerUps,
            laserBeams,
            shield,
            explosions,
            ballAttachedToPaddle
        );
    }

    // Load level
    public void loadLevelNumber(int level) {
        levelLabelRef.setText("Level: " + level);
        this.lives = GameConstants.INITIAL_LIVES;
        if (livesLabelRef != null) {
            livesLabelRef.setText("Lives: " + this.lives);
            livesLabelRef.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        }
        String file = "level" + level + ".txt";
        this.bricks = LevelLoader.loadLevel(file);
        
        // Setup explosion handlers cho ExplodeBricks
        for (Brick brick : bricks) {
            if (brick instanceof ExplodeBrick) {
                ((ExplodeBrick) brick).setExplosionHandler(this::handleExplosion);
            }
        }
        
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

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
        if (!gameRunning) {
            cleanup();
        }
    }

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    // Setup collision callbacks cho CollisionManager
    private void setupCollisionCallbacks() {
        // Callback khi ball rơi ra ngoài
        collisionManager.setOnBallFallOut(() -> handleBallFallOut());
        
        // Callback khi brick bị phá hủy
        collisionManager.setOnBrickDestroyed(data -> {
            CollisionManager.BrickCollisionData brickData = (CollisionManager.BrickCollisionData) data;
            handleBrickDestroyed(brickData.brick, brickData.anyBricksLeft);
        });
        
        // Callback khi hoàn thành level
        collisionManager.setOnLevelComplete(() -> {
            Platform.runLater(() -> handleLevelCompletion());
        });
    }
    
    // Setup power-up callbacks cho PowerUpManager
    private void setupPowerUpCallbacks() {
        // Callback khi extra life được activate
        powerUpManager.setOnExtraLife(() -> {
            if (lives < GameConstants.MAX_LIVE) {
                lives++;
            }
            if (livesLabelRef != null) {
                livesLabelRef.setText("Lives: " + lives);
                if (lives == GameConstants.MAX_LIVE) {
                    livesLabelRef.setStyle("-fx-text-fill: red; -fx-font-size: 16; -fx-font-weight: bold;");
                } else {
                    livesLabelRef.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
                }
            }
        });
        
        // Callback khi shield được activate
        powerUpManager.setOnShieldActivated(data -> {
            if (data instanceof Shield) {
                shield = (Shield) data;
            }
        });
    }
    
    // Xử lý khi brick bị phá hủy
    private void handleBrickDestroyed(Brick brick, boolean anyBricksLeft) {
        // Cộng điểm
        score += brick.getPoints();
        if (scoreLabelRef != null) {
            scoreLabelRef.setText("Score: " + score);
        }
        
        // 20% rơi power-up (nếu còn gạch khác)
        if (anyBricksLeft && random.nextDouble() < GameConstants.POWER_UP_RATE) {
            powerUpManager.spawnPowerUp(
                brick.getPosition().getX() + GameConstants.BRICK_WIDTH / 2,
                brick.getPosition().getY()
            );
        }
    }
    
    // Xử lý khi ball rơi ra ngoài màn hình
    private void handleBallFallOut() {
        // Reset ball position
        double resetX = paddle.getPosition().getX();
        double resetY = paddle.getPosition().getY() - (paddle.getHeight() / 2.0) - ball.getRadius();
        ball.setPosition(new Vector2D(resetX, resetY));
        ball.setVelocity(new Vector2D(0.0, 0.0));
        ballAttachedToPaddle = true;

        // Giảm mạng
        lives = Math.max(0, lives - 1);
        if (this.livesLabelRef != null) {
            this.livesLabelRef.setText("Lives: " + lives);
            if (lives == GameConstants.MAX_LIVE) {
                livesLabelRef.setStyle("-fx-text-fill: red; -fx-font-size: 16; -fx-font-weight: bold;");
            } else {
                livesLabelRef.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
            }
        }

        // Check game over
        if (lives <= 0) {
            this.totalScore = this.score;
            this.setGameRunning(false);
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                    Parent root = loader.load();
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
    
    // Cleanup resources
    public void cleanup() {
        // Delegate laser cleanup to PowerUpManager
        if (powerUpManager != null) {
            powerUpManager.cleanup();
        }
    }

    public void handleSpacePressed() {
        if (ballAttachedToPaddle) {
            ballAttachedToPaddle = false;
            chargePulse = 0;
            chargeIncreasing = true;
            double diag = GameConstants.BALL_SPEED / Math.sqrt(2.0);
            double dir = 0;

            if (leftPressed && !rightPressed) dir = -1;
            else if (rightPressed && !leftPressed) dir = 1;
            else dir = Math.random() < 0.5 ? -1 : 1; // nếu paddle đứng yên thì ngẫu nhiên

            ball.setVelocity(new Vector2D(diag * dir, -diag));
        }
    }
}
