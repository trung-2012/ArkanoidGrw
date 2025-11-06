package game.arkanoid.utils;

public class GameSettings {
    // Singleton instance - volatile để đảm bảo thread-safety
    private static volatile GameSettings instance;
    
    // Instance fields 
    private String selectedBall;
    private String selectedPaddle;
    private String selectedBullet;
    
    // Private constructor để ngăn chặn khởi tạo bên ngoài
    private GameSettings() {
        // Khởi tạo giá trị mặc định
        this.selectedBall = "/game/arkanoid/images/Ball.png";
        this.selectedPaddle = "/game/arkanoid/images/Paddle.png";
        this.selectedBullet = "/game/arkanoid/images/bulletPaddle.png";
    }
    
    // Public method để lấy instance
    public static GameSettings getInstance() {
        if (instance == null) {
            synchronized (GameSettings.class) {
                if (instance == null) {
                    instance = new GameSettings();
                }
            }
        }
        return instance;
    }
    
    // Getter và Setter cho selectedBall
    public String getSelectedBall() {
        return selectedBall;
    }
    
    public void setSelectedBall(String path) {
        this.selectedBall = path;
    }
    
    // Getter và Setter cho selectedPaddle
    public String getSelectedPaddle() {
        return selectedPaddle;
    }
    
    public void setSelectedPaddle(String path) {
        this.selectedPaddle = path;
        // Tự động cập nhật bullet tương ứng với paddle
        updateBulletForPaddle(path);
    }
    
    // Getter và Setter cho selectedBullet
    public String getSelectedBullet() {
        return selectedBullet;
    }
    
    public void setSelectedBullet(String path) {
        this.selectedBullet = path;
    }
    
    /**
     * Tự động chọn bullet phù hợp với paddle
     */
    private void updateBulletForPaddle(String paddlePath) {
        if (paddlePath.contains("Paddle1.png")) {
            this.selectedBullet = "/game/arkanoid/images/bulletPaddle1.png";
        } else if (paddlePath.contains("Paddle2.png")) {
            this.selectedBullet = "/game/arkanoid/images/bulletPaddle2.png";
        } else if (paddlePath.contains("Paddle3.png")) {
            this.selectedBullet = "/game/arkanoid/images/bulletPaddle3.png";
        } else if (paddlePath.contains("Paddle4.png")) {
            this.selectedBullet = "/game/arkanoid/images/bulletPaddle4.png";
        } else if (paddlePath.contains("Paddle5.png")) {
            this.selectedBullet = "/game/arkanoid/images/bulletPaddle5.png";
        } else {
            this.selectedBullet = "/game/arkanoid/images/bulletPaddle.png";
        }
    }
    
    // Reset method cho testing
    public void reset() {
        this.selectedBall = "/game/arkanoid/images/Ball.png";
        this.selectedPaddle = "/game/arkanoid/images/Paddle.png";
        this.selectedBullet = "/game/arkanoid/images/bulletPaddle.png";
    }
}
