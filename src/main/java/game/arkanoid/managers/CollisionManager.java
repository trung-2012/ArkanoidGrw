package game.arkanoid.managers;

import game.arkanoid.models.*;
import game.arkanoid.utils.GameConstants;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

import java.util.List;

public class CollisionManager {
    
    // Game objects
    private Ball ball;
    private Paddle paddle;
    private List<Brick> bricks;
    private Shield shield;
    private Canvas canvas;
    
    // Collision callbacks
    private CollisionCallback onBallFallOut;
    private CollisionCallbackWithData onBrickDestroyed;
    private CollisionCallback onLevelComplete;
    
    /**
     * Constructor
     */
    public CollisionManager(Ball ball, Paddle paddle, List<Brick> bricks, Canvas canvas) {
        this.ball = ball;
        this.paddle = paddle;
        this.bricks = bricks;
        this.canvas = canvas;
    }
    
    // Kiểm tra tất cả các va chạm
    public void checkAllCollisions() {
        if (ball == null) return;
        
        double screenWidth = canvas != null ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double screenHeight = canvas != null ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;
        
        // ball, walls
        checkBallWallCollision(screenWidth, screenHeight);

        // ball, paddle
        checkBallPaddleCollision();

        // ball, shield
        checkBallShieldCollision();

        // ball, bricks
        checkBallBrickCollision();
    }
    
    // Check ball collision với walls
    private void checkBallWallCollision(double screenWidth, double screenHeight) {
        boolean ballFallOut = ball.collideWithWall(screenWidth, screenHeight);
        
        if (ballFallOut && onBallFallOut != null) {
            // Callback để GameEngine xử lý (reset ball, lose life, game over)
            onBallFallOut.onCollision();
        }
    }
    
    // Check ball collision với paddle
    private void checkBallPaddleCollision() {
        if (paddle != null) {
            ball.collideWith(paddle);
        }
    }
    
    // Check ball collision với shield
    private void checkBallShieldCollision() {
        if (shield != null && shield.collidesWith(ball)) {
            ball.reverseVelocityY();
            shield.hit();
            
            if (shield.isBroken()) {
                shield = null;
            }
        }
    }
    
    // Check ball collision với bricks
    private void checkBallBrickCollision() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                if (ball.collideWith(brick)) {
                    
                    // Check xem còn gạch nào không
                    boolean anyBricksLeft = checkAnyBricksLeft();
                    
                    // Nếu gạch bị phá hủy sau khi nhận damage
                    if (brick.isDestroyed() && onBrickDestroyed != null) {
                        // Callback để GameEngine xử lý (add score, spawn power-up)
                        BrickCollisionData data = new BrickCollisionData(brick, anyBricksLeft);
                        onBrickDestroyed.onCollision(data);
                    }
                    
                    // Check level completion
                    if (!checkAnyBricksLeft() && onLevelComplete != null) {
                        onLevelComplete.onCollision();
                    }
                    
                    break; // Chỉ xử lý 1 collision per frame
                }
            }
        }
    }
    
    // Check laser beam collision với bricks
    public boolean checkLaserBrickCollision(LaserBeam beam) {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && beam.intersects(brick)) {
                brick.takeDamage();
                
                if (brick.isDestroyed() && onBrickDestroyed != null) {
                    boolean anyBricksLeft = checkAnyBricksLeft();
                    BrickCollisionData data = new BrickCollisionData(brick, anyBricksLeft);
                    onBrickDestroyed.onCollision(data);
                    
                    // Check level completion
                    if (!anyBricksLeft && onLevelComplete != null) {
                        Platform.runLater(() -> onLevelComplete.onCollision());
                    }
                }
                
                return true; // Hit detected
            }
        }
        return false; // No hit
    }
    
    // Check power-up collision với paddle
    public boolean checkPowerUpPaddleCollision(PowerUp powerUp) {
        if (paddle != null && powerUp.intersects(paddle)) {
            return true; // Collected
        }
        return false;
    }
    
    // Helper: Check xem còn gạch nào chưa bị phá không
    private boolean checkAnyBricksLeft() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return true;
            }
        }
        return false;
    }
    
    // Getters & Setters
    
    public void setBall(Ball ball) {
        this.ball = ball;
    }
    
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
    
    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }
    
    public void setShield(Shield shield) {
        this.shield = shield;
    }

    public Shield getShield() {
        return this.shield;
    }
    
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    
    // CALLBACKS
    
    public void setOnBallFallOut(CollisionCallback callback) {
        this.onBallFallOut = callback;
    }
    
    public void setOnBrickDestroyed(CollisionCallbackWithData callback) {
        this.onBrickDestroyed = callback;
    }
    
    public void setOnLevelComplete(CollisionCallback callback) {
        this.onLevelComplete = callback;
    }
    
    // CALLBACK INTERFACES
    
    // Generic collision callback (no parameter)
    @FunctionalInterface
    public interface CollisionCallback {
        void onCollision();
    }
    
    // Callback với data parameter
    @FunctionalInterface
    public interface CollisionCallbackWithData {
        void onCollision(Object data);
    }
    
    // Data class cho brick collision callback
    public static class BrickCollisionData {
        public final Brick brick;
        public final boolean anyBricksLeft;
        
        public BrickCollisionData(Brick brick, boolean anyBricksLeft) {
            this.brick = brick;
            this.anyBricksLeft = anyBricksLeft;
        }
    }
}
