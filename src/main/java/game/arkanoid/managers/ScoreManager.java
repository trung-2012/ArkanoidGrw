package game.arkanoid.managers;

import game.arkanoid.utils.GameConstants;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class ScoreManager {
    // Score & Lives state
    private int score = 0;
    private int totalScore = 0;
    private int lives = GameConstants.INITIAL_LIVES;
    private int currentLevel = 1;
    
    // UI References
    private Label scoreLabelRef;
    private Label livesLabelRef;
    private Label levelLabelRef;
    
    // Callback interfaces
    public interface GameOverCallback {
        void onGameOver(int finalScore);
    }
    
    public interface LevelCompleteCallback {
        void onLevelComplete();
    }
    
    private GameOverCallback gameOverCallback;
    private LevelCompleteCallback levelCompleteCallback;
    
    public ScoreManager(Label scoreLabel, Label livesLabel, Label levelLabel) {
        this.scoreLabelRef = scoreLabel;
        this.livesLabelRef = livesLabel;
        this.levelLabelRef = levelLabel;
        
        // Initialize UI
        updateScoreLabel();
        updateLivesLabel();
        updateLevelLabel();
    }
    
    // Thêm điểm khi phá hủy gạch
    public void addScore(int points) {
        score += points;
        updateScoreLabel();
    }
    
    // Giảm mạng khi mất bóng
    public boolean loseLife() {
        lives = Math.max(0, lives - 1);
        updateLivesLabel();
        
        // Check game over
        if (lives <= 0) {
            totalScore = score;
            if (gameOverCallback != null) {
                Platform.runLater(() -> gameOverCallback.onGameOver(totalScore));
            }
            return false;
        }
        return true;
    }
    
    // Thêm mạng (nếu chưa đạt max)
    public void addLife() {
        if (lives < GameConstants.MAX_LIVE) {
            lives++;
            updateLivesLabel();
        }
    }
    
    // Hoàn thành level hiện tại
    public void completeLevel() {
        currentLevel++;
        totalScore = score;
        
        if (currentLevel > GameConstants.totalLevels) {
            // Hoàn thành tất cả levels
            if (gameOverCallback != null) {
                Platform.runLater(() -> gameOverCallback.onGameOver(totalScore));
            }
        } else {
            // Chuyển sang level tiếp theo
            if (levelCompleteCallback != null) {
                Platform.runLater(() -> levelCompleteCallback.onLevelComplete());
            }
        }
    }
    
    // Reset điểm về tổng điểm khi bắt đầu lại level hiện tại
    public void resetCurrentLevel() {
        score = totalScore;
        updateScoreLabel();
    }
    
    // Load level mới
    public void loadLevel(int level) {
        this.currentLevel = level;
        this.lives = GameConstants.INITIAL_LIVES;
        updateLevelLabel();
        updateLivesLabel();
    }
    
    // Cập nhật score label trên UI
    private void updateScoreLabel() {
        if (scoreLabelRef != null) {
            scoreLabelRef.setText("Score: " + score);
        }
    }
    
    // Cập nhật lives label trên UI
    private void updateLivesLabel() {
        if (livesLabelRef != null) {
            // Tạo chuỗi trái tim dựa trên số lives
            StringBuilder heartsBuilder = new StringBuilder();
            for (int i = 0; i < lives; i++) {
                heartsBuilder.append("❤ ");
            }
            String hearts = heartsBuilder.toString().trim();
            
            // Nếu không còn lives, hiển thị text
            if (lives <= 0) {
                livesLabelRef.setText("");
            } else {
                livesLabelRef.setText(hearts);
                
                // Màu vàng nếu lives = MAX_LIVE (5), màu đỏ nếu lives < 5
                if (lives == GameConstants.MAX_LIVE) {
                    livesLabelRef.setStyle("-fx-text-fill: gold; -fx-font-size: 20; -fx-font-weight: bold;");
                } else {
                    livesLabelRef.setStyle("-fx-text-fill: red; -fx-font-size: 20; -fx-font-weight: bold;");
                }
            }
        }
    }
    
    // Cập nhật level label trên UI
    private void updateLevelLabel() {
        if (levelLabelRef != null) {
            levelLabelRef.setText("Level: " + currentLevel);
        }
    }
    
    // Getters
    public int getScore() {
        return score;
    }
    
    public int getTotalScore() {
        return totalScore;
    }
    
    public int getLives() {
        return lives;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    // Setters cho callbacks
    public void setGameOverCallback(GameOverCallback callback) {
        this.gameOverCallback = callback;
    }
    
    public void setLevelCompleteCallback(LevelCompleteCallback callback) {
        this.levelCompleteCallback = callback;
    }
    
    // Setters cho labels
    public void setScoreLabel(Label label) {
        this.scoreLabelRef = label;
        updateScoreLabel();
    }
    
    public void setLivesLabel(Label label) {
        this.livesLabelRef = label;
        updateLivesLabel();
    }
    
    public void setLevelLabel(Label label) {
        this.levelLabelRef = label;
        updateLevelLabel();
    }
}
