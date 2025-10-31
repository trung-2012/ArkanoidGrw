package game.arkanoid.utils;

public class GameSettings {
    private static String selectedBall = "/game/arkanoid/images/Ball.png";
    private static String selectedPaddle = "/game/arkanoid/images/Paddle.png";

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
    }
}