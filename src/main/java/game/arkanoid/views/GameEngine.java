package game.arkanoid.views;

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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameEngine extends AnimationTimer {
    // Thread-safe collections for concurrent access
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final List<LaserBeam> laserBeams = new CopyOnWriteArrayList<>();
    private final List<ExplosionEffect> explosions = new CopyOnWriteArrayList<>();
    
    private final Random random = new Random();
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks = new ArrayList<>();
    private int currentLevel = 1;
    private boolean gameRunning;
    private int lives = GameConstants.INITIAL_LIVES;
    private int score = 0;
    private int totalScore = 0;
    
    // Thread-safe boolean for laser state
    private final AtomicBoolean laserActive = new AtomicBoolean(false);
    private Shield shield;
    private ScheduledExecutorService laserScheduler;

    private Label scoreLabelRef;
    private Label livesLabelRef;
    private Label levelLabelRef;
    private game.arkanoid.controllers.MainController mainController;

    private Canvas canvas;
    private GraphicsContext gc;
    private Image paddleImage;
    private Image ballImage;
    private Image bulletImage;
    private Image brickNormalImage;
    private Image brickWoodImage;
    private Image brickIronImage;
    private Image brickGoldImage;
    private Image brickInsaneImage;
    private Image brickExplodeImage;
    private Image explosionEffectImage;
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
        updatePowerUps();
        checkCollisions();
        render();
    }

    // Reload skin từ GameSettings (gọi khi user thay đổi trong Settings)
    public void reloadSkins() {
        try {
            this.ballImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBall()));
            this.paddleImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedPaddle()));
            this.bulletImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBullet()));
            System.out.println("Đã reload skins: Ball=" + GameSettings.getSelectedBall() + ", Paddle="
                    + GameSettings.getSelectedPaddle() + ", Bullet=" + GameSettings.getSelectedBullet());
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
            this.bulletImage = new Image(getClass().getResourceAsStream(GameSettings.getSelectedBullet()));
        } catch (Exception e) {
            System.err.println("Không thể load skin đã chọn, dùng mặc định: " + e.getMessage());
            // Fallback về skin mặc định
            this.ballImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Ball.png"));
            this.paddleImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/Paddle.png"));
            this.bulletImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/bulletPaddle.png"));
        }

        // Load ảnh gạch
        try {
            this.brickNormalImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickNormal.png"));
            this.brickWoodImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickWood.png"));
            this.brickIronImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickIron.png"));
            this.brickGoldImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickGold.png"));
            this.brickInsaneImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/BrickInsane.png"));
            this.brickExplodeImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/ExplodeBrick.png"));
            this.explosionEffectImage = new Image(getClass().getResourceAsStream("/game/arkanoid/images/ExplodeEffect.png"));
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
        // Khởi tạo ScheduledExecutorService cho laser
        laserScheduler = Executors.newSingleThreadScheduledExecutor();

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

        // Xóa tất cả power-ups đang rơi
        powerUps.clear();

        // Xóa tất cả laser beams
        laserBeams.clear();

        // Xóa shield nếu có
        shield = null;

        // Tắt laser active
        laserActive.set(false);

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
            // Đặt lại vị trí bóng trên paddle, chờ Space để bắn ra
            double resetX = paddle.getPosition().getX();
            double resetY = paddle.getPosition().getY() - (paddle.getHeight() / 2.0) - ball.getRadius();
            ball.setPosition(new Vector2D(resetX, resetY));
            ball.setVelocity(new Vector2D(0.0, 0.0));
            ballAttachedToPaddle = true;

            // giảm mạng và cập nhật label
            lives = Math.max(0, lives - 1);
            if (this.livesLabelRef != null) {
                this.livesLabelRef.setText("Lives: " + lives);
                if (lives == GameConstants.MAX_LIVE) {
                    livesLabelRef.setStyle("-fx-text-fill: red; -fx-font-size: 16; -fx-font-weight: bold;");
                } else {
                    livesLabelRef.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
                }
            }

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
                        score += brick.getPoint();
                        
                        // Xử lý nổ nếu là gạch EXPLODE
                        if (brick.getType() == BrickType.EXPLODE) {
                            handleExplosion(brick);
                        }
                        
                        // 20% rơi power-up
                        // nếu còn 1 viên gạch thì sẽ không rơi powerUp
                        if (random.nextDouble() < GameConstants.POWER_UP_RATE && anyLeft) {
                            PowerUpType type = getRandomPowerUpType();
                            PowerUp powerUp = new PowerUp(brick.getPosition().getX() + GameConstants.BRICK_WIDTH / 2,
                                    brick.getPosition().getY(), type);
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
                        Platform.runLater(() -> handleLevelCompletion());
                    }
                    break; // Chỉ xử lý một va chạm mỗi frame
                }
            }
        }

    }

    private void handleExplosion(Brick explodedBrick) {
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
                score += brick.getPoint();
                explosions.add(new ExplosionEffect(brick.getPosition(), explosionEffectImage));
                
                if (brick.getType() == BrickType.EXPLODE) {
                    handleExplosion(brick);
                }
            }
        }
        
        if (scoreLabelRef != null) {
            scoreLabelRef.setText("Score: " + score);
        }
    }

    private PowerUpType getRandomPowerUpType() {
        double rand = random.nextDouble();
        if (rand < 0.1)
            return PowerUpType.EXTRA_LIFE; // 10%
        else if (rand < 0.55)
            return PowerUpType.LASER; // 45%
        else
            return PowerUpType.SHIELD; // 45%
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
                activateLaser();
                break;

            case EXTRA_LIFE:
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
                break;
            case SHIELD:
                shield = new Shield(0, canvas.getHeight() - GameConstants.SHIELD_HEIGHT, canvas.getWidth(),
                        GameConstants.SHIELD_HEIGHT);
        }
    }

    private void activateLaser() {
        // Atomic check-and-set để tránh activate nhiều lần
        if (!laserActive.compareAndSet(false, true)) {
            return;  // active r thì th
        }

        if (laserScheduler == null || laserScheduler.isShutdown()) {
            laserScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "LaserThread");
                t.setDaemon(true);  // Daemon thread tự động cleanup
                return t;
            });
        }

        laserScheduler.schedule(() -> {
            int shots = GameConstants.NUM_OF_BULLETS;
            for (int i = 0; i < shots && laserActive.get(); i++) {
                Platform.runLater(() -> {
                    double px = paddle.getPosition().getX();
                    double py = paddle.getPosition().getY() - paddle.getHeight() / 2;
                    double offset = paddle.getWidth() / 2 - 10;
                    laserBeams.add(new LaserBeam(new Vector2D(px - offset, py), bulletImage));
                    laserBeams.add(new LaserBeam(new Vector2D(px + offset, py), bulletImage));
                });
                try {
                    Thread.sleep(GameConstants.COOL_DOWN_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            laserActive.set(false);
        }, 0, TimeUnit.MILLISECONDS);
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
        // Cập nhật power-ups đang rơi
        Iterator<PowerUp> iter = powerUps.iterator();
        while (iter.hasNext()) {
            PowerUp p = iter.next();
            p.update(); // rơi xuống
            // Nếu power-up rơi chạm paddle
            if (paddle != null && p.intersects(paddle)) {
                activatePowerUp(p); // kích hoạt hiệu ứng
                iter.remove(); // xóa power-up khỏi danh sách
            } else if (p.getY() > canvas.getHeight()) {
                iter.remove(); // xóa nếu rơi khỏi màn hình
            }
        }

        // Cập nhật các tia laser
        Iterator<LaserBeam> laserIter = laserBeams.iterator();
        while (laserIter.hasNext()) {
            LaserBeam beam = laserIter.next();
            beam.update();

            boolean hit = false;
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && beam.intersects(brick)) {
                    brick.takeDamage();
                    if (brick.isDestroyed()) {
                        score += brick.getPoint();
                        
                        // Xử lý nổ nếu là gạch EXPLODE
                        if (brick.getType() == BrickType.EXPLODE) {
                            handleExplosion(brick);
                        }
                        
                        Platform.runLater(() -> scoreLabelRef.setText("Score: " + score));

                        boolean anyLeft = false;
                        for (Brick b : bricks) {
                            if (!b.isDestroyed()) {
                                anyLeft = true;
                                break;
                            }
                        }

                        if (!anyLeft) {
                            Platform.runLater(() -> handleLevelCompletion());
                        }
                    }
                    hit = true;
                    break;
                }
            }

            if (hit || beam.isOffScreen(canvas.getHeight())) {
                laserIter.remove();
            }
        }
        
        // Cập nhật hiệu ứng nổ
        Iterator<ExplosionEffect> explosionIter = explosions.iterator();
        while (explosionIter.hasNext()) {
            ExplosionEffect explosion = explosionIter.next();
            explosion.update();
            if (explosion.isFinished()) {
                explosionIter.remove();
            }
        }
    }

    private void handleLevelCompletion() {
        // Xóa tất cả power-ups đang rơi
        powerUps.clear();

        // Xóa tất cả laser beams
        laserBeams.clear();

        // Xóa shield nếu có
        shield = null;

        // Tắt laser active
        laserActive.set(false);

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
                    case EXPLODE:
                        img = brickExplodeImage;
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

// echo laser trail (compact + behind ball)
        List<Vector2D> t = ball.getTrail();
        for (int i = 0; i < t.size(); i++) {
            Vector2D p = t.get(i);

            double progress = (double) i / t.size();  // 0 -> 1 (đuôi -> đầu)
            double alpha = progress * 0.8;    // fade mạnh từ đầu
            gc.setGlobalAlpha(alpha);

            gc.setFill(Color.web("#a0cfff", alpha));

            double trailSize = ball.getRadius() * 2;

            gc.fillOval(
                    p.getX() - trailSize / 2,
                    p.getY() - trailSize / 2,
                    trailSize,
                    trailSize
            );
        }
        gc.setGlobalAlpha(1.0);
        // charge aura
        if (ballAttachedToPaddle) {
            double bx = ball.getPosition().getX();
            double by = ball.getPosition().getY();
            double r = ball.getRadius();

            double auraSize = r * (2.2 + chargePulse * 1.5);
            double alpha = 0.35 + chargePulse * 0.5;

            gc.setGlobalAlpha(alpha);
            gc.setFill(Color.web("#a6f6ff", alpha)); // neon magenta
            gc.fillOval(
                    bx - auraSize,
                    by - auraSize,
                    auraSize * 2,
                    auraSize * 2
            );
            gc.setGlobalAlpha(1.0);

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
        // vẽ power up
        for (PowerUp p : powerUps) {
            Image img = p.getImage();
            if (img != null) {
                gc.drawImage(img, p.getX() - p.getSize() / 2, p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
            } else {
                gc.setFill(Color.LIMEGREEN);
                gc.fillOval(p.getX() - p.getSize() / 2, p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
            }
        }
        // vẽ laserBeam
        for (LaserBeam beam : laserBeams) {
            beam.render(gc);
        }
        // vẽ shield
        if (shield != null) {
            shield.draw(gc);
        }
        
        // vẽ hiệu ứng nổ
        for (ExplosionEffect explosion : explosions) {
            explosion.render(gc);
        }
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

    public void cleanup() {
        laserActive.set(false);
        if (laserScheduler != null && !laserScheduler.isShutdown()) {
            laserScheduler.shutdown();
            try {
                if (!laserScheduler.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    laserScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                laserScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
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
