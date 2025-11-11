package game.arkanoid.views;

import game.arkanoid.managers.*;
import game.arkanoid.models.*;
import game.arkanoid.powerup.LaserBeam;
import game.arkanoid.powerup.PowerUp;
import game.arkanoid.powerup.Shield;
import game.arkanoid.utils.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEngine extends AnimationTimer {
    private static final long COUNTDOWN_DURATION = 1000;
    private static final long INTRO_DURATION = 3000;
    private static final long LEVEL_CLEAR_DURATION = 2000;
    private static final long LEVEL_CLEAR_DELAY = 1000;
    // GAME OBJECTS
    // Thread-safe collections for concurrent access
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final List<LaserBeam> laserBeams = new CopyOnWriteArrayList<>();
    private final List<ExplosionEffect> explosions = new CopyOnWriteArrayList<>();
    private final List<DebrisEffect> debrisEffects = new CopyOnWriteArrayList<>();
    private final List<Ball> balls = new CopyOnWriteArrayList<>();
    // MANAGERS
    private CollisionManager collisionManager;
    private RenderManager renderManager;
    private PowerUpManager powerUpManager;
    private InputManager inputManager;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    // MAIN GAME OBJECTS
    private Ball mainBall;
    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    // GAME STATE
    private boolean gameRunning;
    private Shield shield;
    private game.arkanoid.controllers.MainController mainController;
    private Canvas canvas;
    // IMAGES
    private Image paddleImage;
    private Image ballImage;
    private Image bulletImage;
    private Image explosionEffectImage;
    // INPUT STATE
    private boolean ballAttachedToPaddle = true;
    private double chargePulse = 0;
    private boolean chargeIncreasing = true;
    // SCREEN SHAKE EFFECT
    private double shakeOffsetX = 0;
    private double shakeOffsetY = 0;
    private int shakeFramesLeft = 0;
    private double shakeIntensity = 0;
    // COUNTDOWN STATE
    private boolean countdownActive = false;
    private int countdownNumber = 3;
    private long countdownStartTime = 0;
    // START
    private boolean introAnimationActive = false;
    private long introStartTime = 0;
    // LEVEL CLEAR ANIMATION
    private boolean levelClearAnimActive = false;
    private long levelClearStartTime = 0;
    private boolean levelClearDelayActive = false;
    private long levelClearDelayStartTime = 0;
    private boolean startShown = false;
    private long startShownTime = 0;

    @Override
    public void handle(long now) {
        // Nếu intro đang chạy
        if (introAnimationActive) {
            renderIntroAnimation();
            return;
        }
        // Nếu đang trong delay trước level clear animation
        if (levelClearDelayActive) {
            updateLevelClearDelay();
            return;
        }
        if (levelClearAnimActive) {
            renderLevelClearAnimation();
            return;
        }
        // Nếu đang countdown, chỉ update và render countdown
        if (countdownActive) {
            updateCountdown();
            renderCountdown();
            return;
        }

        if (!gameRunning)
            return;
        updateGameState();
        if (powerUpManager != null) {
            powerUpManager.updatePowerUps(ballAttachedToPaddle);
            powerUpManager.updateLaserBeams(collisionManager);
        }
        checkCollisions();
        render();
    }

    // Reload skin từ GameSettings (gọi khi user thay đổi trong Settings)
    public void reloadSkins() {
        try {
            GameSettings settings = GameSettings.getInstance();
            this.ballImage = new Image(getClass().getResourceAsStream(settings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(settings.getSelectedPaddle()));
            this.bulletImage = new Image(getClass().getResourceAsStream(settings.getSelectedBullet()));

            // Update images cho RenderManager
            if (renderManager != null) {
                renderManager.setBallImage(ballImage);
                renderManager.setPaddleImage(paddleImage);
            }
            if (powerUpManager != null) {
                powerUpManager.setBulletImage(bulletImage);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi reload skins: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeGame(Canvas canvas, Label scoreLabel, Label livesLabel, Label levelLabel) {
        this.canvas = canvas;

        // Khởi tạo ScoreManager
        this.scoreManager = new ScoreManager(scoreLabel, livesLabel, levelLabel);
        setupScoreCallbacks();

        // Khởi tạo RenderManager
        this.renderManager = new RenderManager(canvas);

        // Khởi tạo PowerUpManager
        this.powerUpManager = new PowerUpManager(powerUps, laserBeams);
        this.powerUpManager.setCanvas(canvas);

        // Khởi tạo SoundManager (Sử dụng Singleton)
        this.soundManager = SoundManager.getInstance();
        // Cung cấp SoundManager cho PowerUpManager
        if (this.soundManager != null) {
            this.powerUpManager.setSoundManager(this.soundManager);
        }
        try {
            soundManager.loadSoundEffect("brick_break", "src/main/resources/game/arkanoid/sounds/brick_break.wav");
            soundManager.loadSoundEffect("lose_life", "src/main/resources/game/arkanoid/sounds/lose_life.wav");
            soundManager.loadSoundEffect("hit_paddle", "src/main/resources/game/arkanoid/sounds/hit_paddle.wav");
            soundManager.loadSoundEffect("shield_hit", "src/main/resources/game/arkanoid/sounds/shield_hit.mp3");
            soundManager.loadSoundEffect("explosion", "src/main/resources/game/arkanoid/sounds/explosion.mp3");
            soundManager.loadSoundEffect("level_complete", "src/main/resources/game/arkanoid/sounds/level_complete.mp3");
            soundManager.loadSoundEffect("extra_life", "src/main/resources/game/arkanoid/sounds/extra_life.mp3");
            soundManager.loadSoundEffect("shield_up", "src/main/resources/game/arkanoid/sounds/shield_up.mp3");
            soundManager.loadSoundEffect("multiball", "src/main/resources/game/arkanoid/sounds/multiball.mp3");
            soundManager.loadSoundEffect("laser_fire", "src/main/resources/game/arkanoid/sounds/laser_fire.mp3");
            soundManager.loadSoundEffect("wall_hit", "src/main/resources/game/arkanoid/sounds/hit_paddle.wav");
            soundManager.loadSoundEffect("endgame", "src/main/resources/game/arkanoid/sounds/endgame.mp3");
            // soundManager.loadSoundEffect("slow", "src/main/resources/game/arkanoid/sounds/slow.mp3");
            // soundManager.loadSoundEffect("fast", "src/main/resources/game/arkanoid/sounds/fast.mp3");
            soundManager.loadSoundEffect("paddle_grow", "src/main/resources/game/arkanoid/sounds/paddle_grow.mp3");
            soundManager.loadSoundEffect("paddle_shrink", "src/main/resources/game/arkanoid/sounds/paddle_shrink.mp3");
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng: Không thể tải file âm thanh. " + e.getMessage());
        }

        // Khởi tạo InputManager (sẽ set paddle sau khi startNewGame)
        this.inputManager = new InputManager(paddle, canvas);
        this.inputManager.setSpaceCallback(() -> {
            // khi Space: bung bóng chính ra
            ballAttachedToPaddle = false;
            chargePulse = 0;
            chargeIncreasing = true;
            if (mainBall != null && mainBall.getVelocity().getY() == 0.0) {
                double diag = GameConstants.BALL_SPEED / Math.sqrt(2.0);
                double dir = Math.random() < 0.5 ? -1 : 1;
                mainBall.setVelocity(new Vector2D(diag * dir, -diag));
            }
        });

        // Load ảnh Ball & Paddle theo skin đã chọn từ GameSettings
        try {
            GameSettings settings = GameSettings.getInstance();
            this.ballImage = new Image(getClass().getResourceAsStream(settings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(settings.getSelectedPaddle()));
            this.bulletImage = new Image(getClass().getResourceAsStream(settings.getSelectedBullet()));
        } catch (Exception e) {
            // Fallback về skin mặc định
            this.ballImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Ball.png"));
            this.paddleImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Paddle.png"));
            this.bulletImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/bulletPaddle.png"));
        }
        renderManager.setBallImage(ballImage);
        renderManager.setPaddleImage(paddleImage);
        powerUpManager.setBulletImage(bulletImage);

        try {
            this.explosionEffectImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/ExplodeEffect.png"));
        } catch (Exception e) {
            System.err.println("Warning: Could not load explosion effect image");
        }

        // canvas focus để nhận phím
        if (this.canvas != null) {
            this.canvas.requestFocus();
        }

        startNewGame();
    }

    // Bắt đầu game mới
    public void startNewGame() {
        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10;

        this.paddle = new Paddle(new Vector2D(px, py));
        this.soundManager.resumeBackgroundMusic();

        double bx = px;
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0);

        Ball newBall = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        newBall.setVelocity(new Vector2D(0.0, 0.0));

        ballAttachedToPaddle = true;

        balls.clear();
        balls.add(newBall);
        this.mainBall = newBall;

        int currentLevel = scoreManager != null ? scoreManager.getCurrentLevel() : 1;
        loadLevelNumber(currentLevel);

        // Khởi tạo CollisionManager và setup callbackss
        this.collisionManager = new CollisionManager(balls, paddle, bricks, canvas);
        setupCollisionCallbacks();

        // Setup PowerUpManager
        powerUpManager.setPaddle(paddle);
        setupPowerUpCallbacks();

        // Update InputManager với paddle mới
        if (inputManager != null) {
            inputManager.setPaddle(paddle);
        }
        // Bắt đầu intro animation
        introAnimationActive = true;
        introStartTime = System.currentTimeMillis();
        // Bắt đầu AnimationTimer và countdown
        this.start();
    }

    // Reset lại màn chơi hiện tại
    public void resetCurrentLevel() {
        // Delegate reset logic cho ScoreManager
        if (scoreManager != null) {
            scoreManager.resetCurrentLevel();
        }

        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10;

        this.paddle = new Paddle(new Vector2D(px, py));

        double bx = px;
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0);

        Ball newBall = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        newBall.setVelocity(new Vector2D(0.0, 0.0));

        ballAttachedToPaddle = true;

        balls.clear();
        balls.add(newBall);
        this.mainBall = newBall;

        // Khởi tạo CollisionManager
        this.collisionManager = new CollisionManager(balls, paddle, bricks, canvas);
        setupCollisionCallbacks();

        // Clear power ups và lasers
        powerUpManager.clearPowerUps();
        powerUpManager.clearLaserBeams();
        powerUpManager.setPaddle(paddle);
        setupPowerUpCallbacks();
        shield = null;

        if (inputManager != null) {
            inputManager.setPaddle(paddle);
        }

        // Clear debris effects
        debrisEffects.clear();

        int currentLevel = scoreManager != null ? scoreManager.getCurrentLevel() : 1;
        loadLevelNumber(currentLevel);

        introAnimationActive = true;
        introStartTime = System.currentTimeMillis();
        startCountdown();
    }

    // MULTI-BALL: clone all balls -> mỗi quả sinh thêm 2 clone (với giới hạn MAX_BALLS = 6)
    private void activateMultiBall() {
        // Check nếu đã đạt max balls thì không spawn thêm
        if (balls.size() >= GameConstants.MAX_BALLS) {
            return;
        }

        List<Ball> snapshot = new ArrayList<>(balls);
        for (Ball original : snapshot) {
            // Kiểm tra trước khi thêm mỗi clone
            if (balls.size() >= GameConstants.MAX_BALLS) {
                break;
            }

            Ball ball1 = cloneBall(original, +1.5);
            balls.add(ball1);

            if (balls.size() >= GameConstants.MAX_BALLS) {
                break;
            }

            Ball ball2 = cloneBall(original, -1.5);
            balls.add(ball2);
        }
    }

    private Ball cloneBall(Ball base, double offsetVX) {
        Ball ball = new Ball(new Vector2D(base.getPosition().getX(), base.getPosition().getY()), base.getRadius());
        double vx = base.getVelocity().getX();
        double vy = base.getVelocity().getY();
        ball.setVelocity(new Vector2D(vx + offsetVX, vy));
        ball.setOriginal(false);
        return ball;
    }

    // Kiểm tra va chạm - Delegate cho CollisionManager
    public void checkCollisions() {
        if (collisionManager == null) return;

        // Update collision manager với game state hiện tại
        collisionManager.setBricks(bricks);
        collisionManager.setShield(shield);

        collisionManager.setBallAttached(ballAttachedToPaddle);

        // Delegate collision detection cho CollisionManager
        // Tất cả logic xử lý va chạm (cộng điểm, spawn power-up, level complete)
        // được handle thông qua callbacks
        collisionManager.checkAllCollisions();

        // Sync lại shield (có thể đã null nếu broken)
        shield = collisionManager.getShield();
    }

    /**
     * Setup explosion handler cho brick.
     * Xử lý cả ExplodeBrick trực tiếp và ExplodeBrick bên trong SecretBrick.
     */
    private void setupExplosionHandler(Brick brick) {
        if (brick instanceof ExplodeBrick) {
            ((ExplodeBrick) brick).setExplosionHandler(this::handleExplosion);
        } else if (brick instanceof SecretBrick secretBrick) {
            // SecretBrick có thể chứa ExplodeBrick bên trong
            Brick disguise = secretBrick.getDisguiseBrick();
            if (disguise instanceof ExplodeBrick) {
                ((ExplodeBrick) disguise).setExplosionHandler(this::handleExplosion);
            }
        }
    }

    /**
     * Xử lý nổ cho ExplodeBrick.
     * Được gọi khi ExplodeBrick bị phá hủy.
     */
    private void handleExplosion(ExplodeBrick explodedBrick) {
        explosions.add(new ExplosionEffect(explodedBrick.getPosition(), explosionEffectImage));

        if (soundManager != null) {
            soundManager.playSoundEffect("explosion");
        }

        // Kích hoạt screen shake khi nổ
        startScreenShake(8.0, 15); // 8 pixels intensity, 15 frames (~0.25 seconds at 60fps)

        // Random 50-50: nổ theo hàng (true) hoặc theo cột (false)
        Random random = new Random();
        boolean explodeRow = random.nextBoolean();
        double ex = explodedBrick.getPosition().getX();
        double ey = explodedBrick.getPosition().getY();

        for (Brick brick : bricks) {
            if (brick == explodedBrick || brick.isDestroyed()) continue;

            double bx = brick.getPosition().getX();
            double by = brick.getPosition().getY();

            boolean shouldExplode = explodeRow ? (Math.abs(by - ey) < 5) : (Math.abs(bx - ex) < 5);
            if (shouldExplode) {
                brick.setDestroyed(true);
                if (scoreManager != null) scoreManager.addScore(brick.getPoints());
                explosions.add(new ExplosionEffect(brick.getPosition(), explosionEffectImage));
                if (brick instanceof ExplodeBrick) handleExplosion((ExplodeBrick) brick);
            }
        }
    }

    /**
     * Kích hoạt hiệu ứng rung màn hình
     *
     * @param intensity Cường độ rung (pixels)
     * @param frames    Số frame rung
     */
    private void startScreenShake(double intensity, int frames) {
        this.shakeIntensity = intensity;
        this.shakeFramesLeft = frames;
    }

    /**
     * Cập nhật hiệu ứng rung màn hình
     */
    private void updateScreenShake() {
        if (shakeFramesLeft > 0) {
            Random random = new Random();
            // Random offset trong phạm vi [-intensity, +intensity]
            shakeOffsetX = (random.nextDouble() * 2 - 1) * shakeIntensity;
            shakeOffsetY = (random.nextDouble() * 2 - 1) * shakeIntensity;
            shakeFramesLeft--;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }
    }

    /**
     * Bắt đầu countdown
     */
    private void startCountdown() {
        countdownActive = true;
        countdownNumber = 3;
        countdownStartTime = System.currentTimeMillis();
        gameRunning = false; // Tạm dừng game trong khi countdown
    }

    /**
     * Cập nhật countdown
     */
    private void updateCountdown() {
        long elapsed = System.currentTimeMillis() - countdownStartTime;
        if (countdownNumber >= 1) {
            if (elapsed >= COUNTDOWN_DURATION) {
                countdownNumber--;
                countdownStartTime = System.currentTimeMillis();

                // chuyen sang start
                if (countdownNumber < 1) {
                    startShown = true;
                    startShownTime = System.currentTimeMillis();
                }
            }
            return;
        }

        if (startShown) {
            long showElapsed = System.currentTimeMillis() - startShownTime;

            if (showElapsed < 800) {
                return;
            }

            startShown = false;
            countdownActive = false;
            gameRunning = true;
        }
    }


    /**
     * Render countdown lên màn hình.
     * Delegate rendering cho RenderManager.
     */
    private void renderCountdown() {
        if (canvas == null || renderManager == null) return;

        long elapsed = System.currentTimeMillis() - countdownStartTime;
        double progress = Math.min(1.0, elapsed / (double) COUNTDOWN_DURATION);

        // Render game state callback
        Runnable renderGameState = () -> {
            renderManager.setChargePulse(chargePulse);
            renderManager.setScreenShake(0, 0);
            renderManager.renderAll(
                    mainBall, paddle, bricks, powerUps, laserBeams,
                    shield, explosions, debrisEffects, balls, ballAttachedToPaddle
            );
        };

        // Delegate countdown animation to RenderManager
        renderManager.renderCountdownAnimation(progress, countdownNumber, renderGameState);
    }

    // Cập nhật trạng thái game
    public void updateGameState() {
        if (inputManager != null) inputManager.updatePaddleMovement();

        // Cập nhật paddle (kiểm tra power-up hết hạn)
        if (paddle != null) {
            paddle.update();
        }
        
        // Cập nhật screen shake effect
        updateScreenShake();

        if (ballAttachedToPaddle && mainBall != null) {
            // pulse aura
            if (chargeIncreasing) {
                chargePulse += 0.01;
                if (chargePulse > 1.0) chargeIncreasing = false;
            } else {
                chargePulse -= 0.01;
                if (chargePulse < 0.0) chargeIncreasing = true;
            }

            mainBall.getTrail().clear();
            double bx = paddle.getPosition().getX();
            double by = paddle.getPosition().getY() - paddle.getHeight() / 2.0 - mainBall.getRadius();
            mainBall.setPosition(new Vector2D(bx, by));
        } else {
            // Update tất cả balls (bao gồm main ball và clone balls)
            for (Ball b : balls) b.update();
        }

        // Update bricks (cần thiết cho SecretBrick transform logic)
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                brick.update();
                // Re-setup explosion handler nếu SecretBrick transform thành ExplodeBrick
                if (brick instanceof SecretBrick) {
                    setupExplosionHandler(brick);
                }
            }
        }

        // Power-ups và lasers được handle bởi PowerUpManager

        // Update shield (tự động damage sau mỗi 5 giây)
        if (shield != null && !shield.isBroken()) {
            shield.update();
            // Kiểm tra nếu shield broken sau update
            if (shield.isBroken()) {
                shield = null;
            }
        }

        // Cập nhật hiệu ứng nổ và xóa các explosion đã kết thúc
        for (ExplosionEffect explosion : explosions) {
            explosion.update();
        }

        // Xóa các explosion đã kết thúc bằng removeIf()
        explosions.removeIf(ExplosionEffect::isFinished);

        // Cập nhật debris effects
        for (DebrisEffect debris : debrisEffects) {
            debris.update();
        }

        // Xóa debris đã kết thúc
        debrisEffects.removeIf(DebrisEffect::isFinished);
    }

    // Được gọi từ ScoreManager callback khi chuyển level
    private void proceedToNextLevel() {
        int nextLevel = scoreManager != null ? scoreManager.getCurrentLevel() : 1;
        loadLevelNumber(nextLevel);

        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double canvasH = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        double px = canvasW / 2.0;
        double py = canvasH - (GameConstants.PADDLE_HEIGHT / 2.0) - 10;

        this.paddle = new Paddle(new Vector2D(px, py));

        double bx = px;
        double by = py - (GameConstants.PADDLE_HEIGHT / 2.0) - (GameConstants.BALL_SIZE / 2.0);
        Ball newBall = new Ball(new Vector2D(bx, by), GameConstants.BALL_SIZE / 2.0);
        newBall.setVelocity(new Vector2D(0.0, 0.0));
        ballAttachedToPaddle = true;

        balls.clear();
        balls.add(newBall);
        mainBall = newBall;

        // Update các managers với paddle và balls mới
        if (inputManager != null) inputManager.setPaddle(paddle);
        powerUpManager.setPaddle(paddle);

        // QUAN TRỌNG: Update CollisionManager với paddle và balls mới!
        if (collisionManager != null) {
            collisionManager.setPaddle(paddle);
            collisionManager.setBalls(balls);
        }
        introAnimationActive = true;
        introStartTime = System.currentTimeMillis();
        // Bắt đầu countdown cho level mới
        startCountdown();
    }

    // Xử lý sự kiện phím bấm
    public void setLeftPressed(boolean pressed) {
        if (inputManager != null) {
            inputManager.setLeftPressed(pressed);
        }
    }

    // Xử lý sự kiện phím bấm
    public void setRightPressed(boolean pressed) {
        if (inputManager != null) {
            inputManager.setRightPressed(pressed);
        }
    }

    // Render game - Delegate cho RenderManager
    public void render() {
        if (renderManager == null) return;

        // Update charge pulse effect
        renderManager.setChargePulse(chargePulse);

        // Update screen shake effect
        renderManager.setScreenShake(shakeOffsetX, shakeOffsetY);

        // Delegate rendering cho RenderManager
        renderManager.renderAll(
                mainBall,
                paddle,
                bricks,
                powerUps,
                laserBeams,
                shield,
                explosions,
                debrisEffects,
                balls,
                ballAttachedToPaddle
        );
    }

    // Load level
    public void loadLevelNumber(int level) {
        // Delegate load level cho ScoreManager để update UI
        if (scoreManager != null) {
            scoreManager.loadLevel(level);
        }

        String file = "level" + level + ".txt";
        this.bricks = LevelLoader.loadLevel(file);

        // Setup explosion handlers cho ExplodeBricks (bao gồm cả trong SecretBrick)
        for (Brick brick : bricks) {
            setupExplosionHandler(brick);
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
    }

    public Ball getBall() {
        return mainBall;
    } // giữ API cũ

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public int getCurrentLevel() {
        return scoreManager != null ? scoreManager.getCurrentLevel() : 1;
    }

    // Setup score callbacks cho ScoreManager
    private void setupScoreCallbacks() {
        // Callback khi game over
        scoreManager.setGameOverCallback(finalScore -> {
            setGameRunning(false);
            if (soundManager != null) {
                soundManager.pauseBackgroundMusic();
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/GameOver.fxml"));
                Parent root = loader.load();
                game.arkanoid.controllers.GameOverController controller = loader.getController();

                // Truyền player từ mainController TRƯỚC KHI set final score
                if (mainController != null && mainController.getCurrentPlayer() != null) {
                    controller.setPlayer(mainController.getCurrentPlayer());
                }

                // Set final score (sẽ cập nhật high score nếu cần)
                controller.setFinalScore(finalScore);

                Stage stage = (Stage) canvas.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Callback khi hoàn thành level (chuyển level tiếp theo)
        scoreManager.setLevelCompleteCallback(this::proceedToNextLevel);
    }

    // Setup collision callbacks cho CollisionManager
    private void setupCollisionCallbacks() {
        // Callback khi ball rơi ra ngoài
        collisionManager.setOnBallFallOut(this::handleBallFallOut);

        // Callback khi brick bị phá hủy
        collisionManager.setOnBrickDestroyed(data -> {
            CollisionManager.BrickCollisionData brickData = (CollisionManager.BrickCollisionData) data;
            handleBrickDestroyed(brickData.brick, brickData.anyBricksLeft);
        });

        // Callback khi hoàn thành level
        collisionManager.setOnLevelComplete(() -> Platform.runLater(this::handleLevelCompletion));

        collisionManager.setOnPaddleHit(() -> {
            if (soundManager != null) soundManager.playSoundEffect("hit_paddle");
        });

        collisionManager.setOnShieldHit(() -> {
            if (soundManager != null) soundManager.playSoundEffect("shield_hit");
        });

        collisionManager.setOnWallHit(() -> {
            if (soundManager != null) {
                soundManager.playSoundEffect("hit_paddle");
            }
        });
    }

    // Setup power-up callbacks cho PowerUpManager
    private void setupPowerUpCallbacks() {
        // Callback khi extra life được activate - Delegate cho ScoreManager
        powerUpManager.setOnExtraLife(() -> {
            if (scoreManager != null) {
                scoreManager.addLife();
            }
            if (soundManager != null) soundManager.playSoundEffect("extra_life");
        });

        // Callback khi shield được activate
        powerUpManager.setOnShieldActivated(data -> {
            if (data instanceof Shield) {
                shield = (Shield) data;
            }
            if (soundManager != null) soundManager.playSoundEffect("shield_up");
        });

        powerUpManager.setOnMultiBall(() -> {
            activateMultiBall();
            if (soundManager != null) soundManager.playSoundEffect("multiball");
        });
        
        // Callback cho WEAK speed power-up
        powerUpManager.setOnWeakSpeed(() -> {
            for (Ball b : balls) {
                b.applyWeakSpeed();
            }
            if (soundManager != null) soundManager.playSoundEffect("slow");
        });
        
        // Callback cho STRONG speed power-up
        powerUpManager.setOnStrongSpeed(() -> {
            for (Ball b : balls) {
                b.applyStrongSpeed();
            }
            if (soundManager != null) soundManager.playSoundEffect("fast");
        });
    }

    // Xử lý khi brick bị phá hủy
    private void handleBrickDestroyed(Brick brick, boolean anyBricksLeft) {
        // Delegate cộng điểm cho ScoreManager
        if (scoreManager != null) {
            scoreManager.addScore(brick.getPoints());
        }

        if (soundManager != null && !(brick instanceof ExplodeBrick)) {
            soundManager.playSoundEffect("brick_break");
        }

        // Tạo debris effect (không áp dụng cho ExplodeBrick vì đã có explosion)
        if (!(brick instanceof ExplodeBrick)) {
            Color brickColor = getBrickColor(brick);
            debrisEffects.add(new DebrisEffect(
                    new Vector2D(
                            brick.getPosition().getX() + brick.getWidth() / 2.0,
                            brick.getPosition().getY() + brick.getHeight() / 2.0
                    ),
                    brickColor
            ));
        }

        // 20% rơi power-up (nếu còn gạch khác)
        if (anyBricksLeft && Math.random() < GameConstants.POWER_UP_RATE) {
            powerUpManager.spawnPowerUp(
                    brick.getPosition().getX() + GameConstants.BRICK_WIDTH / 2.0,
                    brick.getPosition().getY()
            );
        }
    }

    /**
     * Lấy màu sắc đại diện cho loại gạch.
     */
    private Color getBrickColor(Brick brick) {
        if (brick instanceof NormalBrick) {
            return Color.web("#FF6B6B"); // Đỏ nhạt
        } else if (brick instanceof WoodBrick) {
            return Color.web("#8B4513"); // Nâu gỗ
        } else if (brick instanceof IronBrick) {
            return Color.web("#708090"); // Xám thép
        } else if (brick instanceof GoldBrick) {
            return Color.web("#FFD700"); // Vàng
        } else if (brick instanceof InsaneBrick) {
            return Color.web("#4A4A4A"); // Xám đậm
        } else if (brick instanceof SecretBrick) {
            return Color.web("#9400D3"); // Tím
        }
        return Color.GRAY; // Mặc định
    }

    // Xử lý khi ball rơi ra ngoài màn hình
    // Callback này chỉ được gọi khi TẤT CẢ balls đã rơi hết (logic trong CollisionManager)
    private void handleBallFallOut() {
        // Reset ball position
        double resetX = paddle.getPosition().getX();
        double resetY = paddle.getPosition().getY() - paddle.getHeight() / 2.0;

        Ball newBall = new Ball(new Vector2D(resetX, resetY - GameConstants.BALL_SIZE / 2.0),
                GameConstants.BALL_SIZE / 2.0);
        newBall.setVelocity(new Vector2D(0.0, 0.0));

        ballAttachedToPaddle = true;

        // Clear và reset lại về 1 ball
        balls.clear();
        balls.add(newBall);
        mainBall = newBall;

        // Delegate mất mạng cho ScoreManager (sẽ trigger game over callback nếu hết mạng)
        if (scoreManager != null) {
            scoreManager.loseLife();

            if (soundManager != null) {
                soundManager.playSoundEffect("lose_life");
            }
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
        if (inputManager == null || mainBall == null) return;

        inputManager.handleSpacePressed(mainBall, ballAttachedToPaddle);
        // Nếu bóng đã được phóng, cập nhật trạng thái
        if (ballAttachedToPaddle && mainBall.getVelocity().getY() != 0.0) {
            ballAttachedToPaddle = false;
            chargePulse = 0;
            chargeIncreasing = true;
        }
    }

    private void renderIntroAnimation() {
        if (canvas == null || renderManager == null) return;

        long elapsed = System.currentTimeMillis() - introStartTime;
        double progress = elapsed / (double) INTRO_DURATION;
        
        int level = scoreManager != null ? scoreManager.getCurrentLevel() : 1;
        
        renderManager.renderIntroAnimation(progress, level, this::render);
        
        if (progress >= 1.0) {
            introAnimationActive = false;
            startCountdown();
        }
    }

    private void handleLevelCompletion() {
        if (powerUpManager != null) {
            powerUpManager.clearPowerUps();
            powerUpManager.clearLaserBeams();
        }

        shield = null;

        if (soundManager != null) {
            soundManager.playSoundEffect("level_complete");
        }

        startLevelClearAnimation();
    }

    private void startLevelClearAnimation() {
        levelClearDelayActive = true;
        levelClearDelayStartTime = System.currentTimeMillis();
        gameRunning = false; // freeze game
    }

    /**
     * Cập nhật delay trước khi bắt đầu level clear animation.
     * Chờ 1 giây để explosion effects hoàn thành.
     */
    private void updateLevelClearDelay() {
        long elapsed = System.currentTimeMillis() - levelClearDelayStartTime;
        
        // Cập nhật screen shake effect (quan trọng cho explosion)
        updateScreenShake();
        
        // Quan trọng: Vẫn cập nhật hiệu ứng nổ và debris trong lúc delay
        // Cập nhật hiệu ứng nổ và xóa các explosion đã kết thúc
        for (ExplosionEffect explosion : explosions) {
            explosion.update();
        }
        explosions.removeIf(ExplosionEffect::isFinished);
        
        // Cập nhật debris effects
        for (DebrisEffect debris : debrisEffects) {
            debris.update();
        }
        debrisEffects.removeIf(DebrisEffect::isFinished);
        
        // Render game state để hiển thị explosion/debris
        render();
        
        // Sau 1 giây delay, bắt đầu animation
        if (elapsed >= LEVEL_CLEAR_DELAY) {
            levelClearDelayActive = false;
            levelClearAnimActive = true;
            levelClearStartTime = System.currentTimeMillis();
        }
    }

    private void renderLevelClearAnimation() {
        if (canvas == null || renderManager == null) return;

        long elapsed = System.currentTimeMillis() - levelClearStartTime;
        double progress = elapsed / (double) LEVEL_CLEAR_DURATION;
        
        renderManager.renderLevelClearAnimation(progress, this::render);

        // Animation complete -> next level
        if (progress >= 1.0) {
            levelClearAnimActive = false;

            if (scoreManager != null) scoreManager.completeLevel();
        }
    }
}