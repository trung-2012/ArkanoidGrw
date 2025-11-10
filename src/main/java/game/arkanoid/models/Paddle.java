package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.GameConstants;
import javafx.scene.canvas.GraphicsContext;

import static game.arkanoid.utils.GameConstants.*;

public class Paddle extends GameObject {
    private double moveSpeed;
    private double originalWidth; // Lưu kích thước gốc
    private double originalHeight; // Lưu chiều cao gốc
    private PowerUpType currentSizePowerUp = null; // Trạng thái hiện tại
    private long powerUpEndTime = 0; // Mốc thời gian (ms) hết hạn

    // Constructor
    public Paddle(Vector2D position) {
        super(position, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.moveSpeed = PADDLE_SPEED;
        this.originalWidth = PADDLE_WIDTH; // Lưu lại kích thước gốc
        this.originalHeight = PADDLE_HEIGHT; // Lưu lại chiều cao gốc
    }

    @Override
    public void update() {
        if (currentSizePowerUp != null && System.currentTimeMillis() > powerUpEndTime) {
            resetSize(); // Trở về kích thước bình thường
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi GameEngine với paddleImage
    }

    // Di chuyển paddle trái/phải với tốc độ cố định
    public void moveLeft() {
        // Di chuyển paddle trái
        this.position.setX(this.position.getX() - this.moveSpeed);
        // Đảm bảo paddle không đi ra ngoài màn hình
        double half = this.width / 2.0;
        if (this.position.getX() - half < 0)
            this.position.setX(half);
    }

    public void moveRight() {
        // Di chuyển paddle phải
        this.position.setX(this.position.getX() + this.moveSpeed);
    }

    public void grow() {
        this.width = GameConstants.PADDLE_WIDTH_LARGE;
        this.height = GameConstants.PADDLE_HEIGHT_LARGE;
        this.currentSizePowerUp = PowerUpType.PADDLE_GROW;
        // Đặt lại thời gian hết hạn (sau 10 giây kể từ bây giờ)
        this.powerUpEndTime = System.currentTimeMillis() + GameConstants.POWERUP_DURATION;
    }

    public void shrink() {
        this.width = GameConstants.PADDLE_WIDTH_SMALL;
        this.height = GameConstants.PADDLE_HEIGHT_SMALL;
        this.currentSizePowerUp = PowerUpType.PADDLE_SHRINK;
        // Đặt lại thời gian hết hạn
        this.powerUpEndTime = System.currentTimeMillis() + GameConstants.POWERUP_DURATION;
    }

    private void resetSize() {
        this.width = this.originalWidth;
        this.height = this.originalHeight;
        this.currentSizePowerUp = null;
        this.powerUpEndTime = 0;
    }

    // Getters & Setters
    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}