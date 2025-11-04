package game.arkanoid.utils;

public class GameSettings {
    private static String selectedBall = "/game/arkanoid/images/Ball.png";
    private static String selectedPaddle = "/game/arkanoid/images/Paddle.png";
    private static String selectedBullet = "/game/arkanoid/images/bulletPaddle.png";

    public static String getSelectedBall() {
        return selectedBall;
    }

    public static void setSelectedBall(String path) {
        selectedBall = path;
    }

    public static String getSelectedPaddle() {
        return selectedPaddle;
    }

    public static void setSelectedPaddle(String path) {
        selectedPaddle = path;
        // Tự động cập nhật bullet tương ứng với paddle
        updateBulletForPaddle(path);
    }

    public static String getSelectedBullet() {
        return selectedBullet;
    }

    public static void setSelectedBullet(String path) {
        selectedBullet = path;
    }

    // Tự động chọn bullet phù hợp với paddle
    private static void updateBulletForPaddle(String paddlePath) {
        if (paddlePath.contains("Paddle1.png")) {
            selectedBullet = "/game/arkanoid/images/bulletPaddle1.png";
        } else if (paddlePath.contains("Paddle2.png")) {
            selectedBullet = "/game/arkanoid/images/bulletPaddle2.png";
        } else if (paddlePath.contains("Paddle3.png")) {
            selectedBullet = "/game/arkanoid/images/bulletPaddle3.png";
        } else if (paddlePath.contains("Paddle4.png")) {
            selectedBullet = "/game/arkanoid/images/bulletPaddle4.png";
        } else if (paddlePath.contains("Paddle5.png")) {
            selectedBullet = "/game/arkanoid/images/bulletPaddle5.png";
        } else {
            selectedBullet = "/game/arkanoid/images/bulletPaddle.png";
        }
    }
}
