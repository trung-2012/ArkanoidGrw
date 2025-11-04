package game.arkanoid.utils;

public class GameConstants {
    // Kích thước cửa sổ trò chơi
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    // Thông số Paddle
    public static final int PADDLE_WIDTH = 100;
    public static final int PADDLE_HEIGHT = 20;
    public static final double PADDLE_SPEED = 10.0;

    // Thông số Ball
    public static final int BALL_SIZE = 15;
    public static final double BALL_SPEED = 5.0;

    // Thông số Brick
    public static final int BRICK_ROWS = 5;
    public static final int BRICK_COLUMNS = 10;
    public static final int BRICK_WIDTH = 60;
    public static final int BRICK_HEIGHT = 20;
    public static final int BRICK_PADDING = 5;

    // Số mạng ban đầu
    public static final int INITIAL_LIVES = 3;
    // Số mạng tối đa
    public static final int MAX_LIVE = 5;

    // Tốc độ khung hình
    public static final int FRAME_RATE = 60;

    // Tổng số level trong trò chơi
    public static final int totalLevels = 4;

    // Thông số powerUp
    public static final double POWER_UP_RATE = 1; // tỉ lệ rơi ra powerUp (1=100%)
    public static final int NUM_OF_BULLETS = 5; // số lượng đạn
    public static final int COOL_DOWN_TIME = 500;
    public static final int SHIELD_HEIGHT = 10;// thời gian giữa mỗi lần bắn
}
