package game.arkanoid.managers;

import game.arkanoid.models.*;
import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PowerUpManager {
    
    private final Random random = new Random();
    
    // Power-up collections
    private final List<PowerUp> powerUps;
    private final List<LaserBeam> laserBeams;
    
    // Game references
    private Paddle paddle;
    private Canvas canvas;
    private Image bulletImage;
    
    // Laser system
    private ScheduledExecutorService laserScheduler;
    private final AtomicBoolean laserActive = new AtomicBoolean(false);
    
    // Callbacks
    private PowerUpCallback onExtraLife;
    private PowerUpCallbackWithData onShieldActivated;
    
    public PowerUpManager(List<PowerUp> powerUps, List<LaserBeam> laserBeams) {
        this.powerUps = powerUps;
        this.laserBeams = laserBeams;
        this.laserScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "LaserThread");
            t.setDaemon(true);
            return t;
        });
    }
    
    // Spawn power up tại vị trí (x, y)
    public void spawnPowerUp(double x, double y) {
        PowerUpType type = getRandomPowerUpType();
        PowerUp powerUp = new PowerUp(x, y, type);
        powerUps.add(powerUp);
    }
    
    // Random power-up type
    private PowerUpType getRandomPowerUpType() {
        double rand = random.nextDouble();
        if (rand < 0.1)
            return PowerUpType.EXTRA_LIFE; // 10%
        else if (rand < 0.55)
            return PowerUpType.LASER; // 45%
        else
            return PowerUpType.SHIELD; // 45%
    }
    
    // Update tất cả power-ups (movement, collision detection)
    public void updatePowerUps() {
        if (paddle == null || canvas == null) return;
        
        // Update all power-ups movement
        for (PowerUp powerUp : powerUps) {
            powerUp.update();
        }
        
        // Remove collected or off-screen power-ups using removeIf (CopyOnWriteArrayList safe)
        powerUps.removeIf(powerUp -> {
            // Kiểm tra va chạm với paddle
            if (powerUp.intersects(paddle)) {
                activatePowerUp(powerUp);
                return true; // Remove
            }
            
            // Nếu rơi ra ngoài màn hình
            if (powerUp.getY() > canvas.getHeight()) {
                return true; // Remove
            }
            
            return false; // Keep
        });
    }
    
    // Kích hoạt hiệu ứng power-up
    private void activatePowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case LASER:
                activateLaser();
                break;
                
            case EXTRA_LIFE:
                if (onExtraLife != null) {
                    onExtraLife.onActivate();
                }
                break;
                
            case SHIELD:
                if (onShieldActivated != null) {
                    Shield shield = new Shield(
                        0, 
                        canvas.getHeight() - GameConstants.SHIELD_HEIGHT, 
                        canvas.getWidth(),
                        GameConstants.SHIELD_HEIGHT
                    );
                    onShieldActivated.onActivate(shield);
                }
                break;
        }
    }
    
    // Kích hoạt hiệu ứng laser power-up
    private void activateLaser() {
        // Atomic check-and-set để tránh activate nhiều lần
        if (!laserActive.compareAndSet(false, true)) {
            return;  // Đã active rồi thì thôi
        }
        
        if (laserScheduler == null || laserScheduler.isShutdown()) {
            laserScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "LaserThread");
                t.setDaemon(true);
                return t;
            });
        }
        
        laserScheduler.schedule(() -> {
            int shots = GameConstants.NUM_OF_BULLETS;
            for (int i = 0; i < shots && laserActive.get(); i++) {
                Platform.runLater(() -> {
                    if (paddle != null && bulletImage != null) {
                        double px = paddle.getPosition().getX();
                        double py = paddle.getPosition().getY() - paddle.getHeight() / 2;
                        double offset = paddle.getWidth() / 2 - 10;
                        laserBeams.add(new LaserBeam(new Vector2D(px - offset, py), bulletImage));
                        laserBeams.add(new LaserBeam(new Vector2D(px + offset, py), bulletImage));
                    }
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
    
    // Update laser beams
    public void updateLaserBeams(CollisionManager collisionManager) {
        laserBeams.removeIf(beam -> {
            beam.update();
            
            // Kiểm tra va chạm với bricks
            if (collisionManager != null && collisionManager.checkLaserBrickCollision(beam)) {
                return true; // Remove beam nếu hit brick
            }
            
            // Remove nếu ra ngoài màn hình
            return beam.getPosition().getY() < 0;
        });
    }
    
    // Clear all power-ups
    public void clearPowerUps() {
        powerUps.clear();
    }
    
    // Clear all laser beams
    public void clearLaserBeams() {
        laserBeams.clear();
        laserActive.set(false);
    }
    
    // Cleanup resources
    public void cleanup() {
        laserActive.set(false);
        if (laserScheduler != null && !laserScheduler.isShutdown()) {
            laserScheduler.shutdown();
            try {
                if (!laserScheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    laserScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                laserScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // Setters
    
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
    
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    
    public void setBulletImage(Image bulletImage) {
        this.bulletImage = bulletImage;
    }
    
    public void setOnExtraLife(PowerUpCallback callback) {
        this.onExtraLife = callback;
    }
    
    public void setOnShieldActivated(PowerUpCallbackWithData callback) {
        this.onShieldActivated = callback;
    }
    
    // Getters
    
    public boolean isLaserActive() {
        return laserActive.get();
    }
    
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
    
    public List<LaserBeam> getLaserBeams() {
        return laserBeams;
    }
    
    // Callback interfaces
    
    @FunctionalInterface
    public interface PowerUpCallback {
        void onActivate();
    }
    
    @FunctionalInterface
    public interface PowerUpCallbackWithData {
        void onActivate(Object data);
    }
}
