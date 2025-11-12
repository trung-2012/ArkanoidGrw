package game.arkanoid.player_manager;

import game.arkanoid.utils.Vector2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class lưu trữ trạng thái game để save/load.
 * Chỉ lưu: Ball position/velocity, Paddle position, Bricks còn lại, level hiện tại, score, lives
 * Không lưu: Power-ups đang active
 */
public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    // Player info
    private String username;
    
    // Ball data (main ball only)
    private Vector2D ballPosition;
    private Vector2D ballVelocity;
    private double ballRadius;
    
    // Paddle data (không lưu power-up)
    private Vector2D paddlePosition;
    
    // Bricks data
    private List<BrickData> bricks;
    
    // Game state
    private int currentLevel;
    private int score;
    private int totalScore;
    private int lives;
    
    public GameSaveData() {
        this.bricks = new ArrayList<>();
    }
    
    /**
     * Inner class để serialize brick data
     */
    public static class BrickData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String brickType;
        private Vector2D position;
        private int health;
        private boolean destroyed;
        
        public BrickData(String brickType, Vector2D position, int health, boolean destroyed) {
            this.brickType = brickType;
            this.position = position;
            this.health = health;
            this.destroyed = destroyed;
        }
        
        public String getBrickType() {
            return brickType;
        }
        
        public Vector2D getPosition() {
            return position;
        }
        
        public int getHealth() {
            return health;
        }
        
        public boolean isDestroyed() {
            return destroyed;
        }
    }
    
    // Getters and Setters
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Vector2D getBallPosition() {
        return ballPosition;
    }
    
    public void setBallPosition(Vector2D ballPosition) {
        this.ballPosition = ballPosition;
    }
    
    public Vector2D getBallVelocity() {
        return ballVelocity;
    }
    
    public void setBallVelocity(Vector2D ballVelocity) {
        this.ballVelocity = ballVelocity;
    }
    
    public double getBallRadius() {
        return ballRadius;
    }
    
    public void setBallRadius(double ballRadius) {
        this.ballRadius = ballRadius;
    }
    
    public Vector2D getPaddlePosition() {
        return paddlePosition;
    }
    
    public void setPaddlePosition(Vector2D paddlePosition) {
        this.paddlePosition = paddlePosition;
    }
    
    public List<BrickData> getBricks() {
        return bricks;
    }
    
    public void setBricks(List<BrickData> bricks) {
        this.bricks = bricks;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    
    public int getLives() {
        return lives;
    }
    
    public void setLives(int lives) {
        this.lives = lives;
    }
}
