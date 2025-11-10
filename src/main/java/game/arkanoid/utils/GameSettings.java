package game.arkanoid.utils;

import game.arkanoid.controllers.MainController;
import javafx.stage.Stage;

public class GameSettings {
    // Singleton instance - volatile để đảm bảo thread-safety
    private static volatile GameSettings instance;

    // Instance fields 
    private String selectedBall;
    private String selectedPaddle;
    private String selectedBullet;

    // Navigation context - để giữ context khi navigate Settings → Preview → Back
    private MainController currentMainController;
    private Stage currentSettingsStage;

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

    /**
     * Lấy màu trail cho laser dựa trên paddle đang dùng.
     * paddle (nâu), paddle1 (tím), paddle2 (tím), paddle3 (xanh lam nhạt),
     * paddle4 (trắng), paddle5 (cam)
     */
    public String getLaserTrailColor() {
        if (selectedPaddle.contains("Paddle1.png")) {
            return "#b366ff"; // Tím
        } else if (selectedPaddle.contains("Paddle2.png")) {
            return "#cc99ff"; // Tím nhạt hơn
        } else if (selectedPaddle.contains("Paddle3.png")) {
            return "#66d9ff"; // Xanh lam nhạt
        } else if (selectedPaddle.contains("Paddle4.png")) {
            return "#e6f2ff"; // Trắng xanh nhạt
        } else if (selectedPaddle.contains("Paddle5.png")) {
            return "#ffb366"; // Cam
        } else {
            return "#a67c52"; // Nâu (default paddle)
        }
    }

    // Reset method cho testing
    public void reset() {
        this.selectedBall = "/game/arkanoid/images/Ball.png";
        this.selectedPaddle = "/game/arkanoid/images/Paddle.png";
        this.selectedBullet = "/game/arkanoid/images/bulletPaddle.png";
    }

    // Getters & Setters
    public MainController getCurrentMainController() {
        return currentMainController;
    }

    public void setCurrentMainController(MainController controller) {
        this.currentMainController = controller;
    }

    public Stage getCurrentSettingsStage() {
        return currentSettingsStage;
    }

    public void setCurrentSettingsStage(Stage stage) {
        this.currentSettingsStage = stage;
    }

    public void clearNavigationContext() {
        this.currentMainController = null;
        this.currentSettingsStage = null;
    }
}
