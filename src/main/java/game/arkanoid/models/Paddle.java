package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.GameConstants;
import javafx.scene.canvas.GraphicsContext;

import static game.arkanoid.utils.GameConstants.*;

/**
 * Lớp Paddle đại diện cho thanh gỗ điều khiển trong game Arkanoid.
 * Kế thừa từ GameObject và chứa logic di chuyển trái/phải, power-up thay đổi kích thước.
 * 
 * @author ArkanoidGrw
 * @version 1.0
 */
public class Paddle extends GameObject {
    /** Tốc độ di chuyển của paddle */
    private double moveSpeed;
    /** Chiều rộng gốc để reset sau khi power-up hết hạn */
    private double originalWidth;
    /** Chiều cao gốc để reset sau khi power-up hết hạn */
    private double originalHeight;
    /** Loại power-up kích thước đang active (null nếu không có) */
    private PowerUpType currentSizePowerUp = null;
    /** Thời điểm (ms) power-up kích thước hết hạn */
    private long powerUpEndTime = 0;

    /**
     * Constructor khởi tạo Paddle tại vị trí cho trước.
     * 
     * @param position Vị trí trung tâm của paddle
     */
    public Paddle(Vector2D position) {
        super(position, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.moveSpeed = PADDLE_SPEED;
        this.originalWidth = PADDLE_WIDTH;
        this.originalHeight = PADDLE_HEIGHT;
    }

    /**
     * Cập nhật trạng thái paddle.
     * Kiểm tra và reset kích thước khi power-up hết hạn.
     */
    @Override
    public void update() {
        if (currentSizePowerUp != null && System.currentTimeMillis() > powerUpEndTime) {
            resetSize();
        }
    }

    /**
     * Render paddle lên canvas.
     * Logic render được xử lý bởi RenderManager với paddleImage.
     * 
     * @param gc GraphicsContext để vẽ
     */
    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi RenderManager với paddleImage
    }

    /**
     * Di chuyển paddle sang trái.
     * Tự động giới hạn không cho paddle vượt ra ngoài màn hình.
     */
    public void moveLeft() {
        this.position.setX(this.position.getX() - this.moveSpeed);
        // Đảm bảo paddle không đi ra ngoài màn hình
        double half = this.width / 2.0;
        if (this.position.getX() - half < 0)
            this.position.setX(half);
    }

    /**
     * Di chuyển paddle sang phải.
     * Boundary check được xử lý bởi InputManager.
     */
    public void moveRight() {
        this.position.setX(this.position.getX() + this.moveSpeed);
    }

    /**
     * Áp dụng hiệu ứng GROW power-up (tăng kích thước paddle).
     * Tự động hết hạn sau POWERUP_DURATION ms.
     */
    public void grow() {
        this.width = GameConstants.PADDLE_WIDTH_LARGE;
        this.height = GameConstants.PADDLE_HEIGHT_LARGE;
        this.currentSizePowerUp = PowerUpType.PADDLE_GROW;
        this.powerUpEndTime = System.currentTimeMillis() + GameConstants.POWERUP_DURATION;
    }

    /**
     * Áp dụng hiệu ứng SHRINK power-up (giảm kích thước paddle).
     * Tự động hết hạn sau POWERUP_DURATION ms.
     */
    public void shrink() {
        this.width = GameConstants.PADDLE_WIDTH_SMALL;
        this.height = GameConstants.PADDLE_HEIGHT_SMALL;
        this.currentSizePowerUp = PowerUpType.PADDLE_SHRINK;
        this.powerUpEndTime = System.currentTimeMillis() + GameConstants.POWERUP_DURATION;
    }

    /**
     * Reset kích thước về giá trị gốc.
     * Được gọi tự động khi power-up hết hạn.
     */
    private void resetSize() {
        this.width = this.originalWidth;
        this.height = this.originalHeight;
        this.currentSizePowerUp = null;
        this.powerUpEndTime = 0;
    }

    // Getters & Setters
    
    /**
     * Lấy tốc độ di chuyển của paddle.
     * 
     * @return Tốc độ di chuyển (pixels/frame)
     */
    public double getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * Thiết lập tốc độ di chuyển mới cho paddle.
     * 
     * @param moveSpeed Tốc độ di chuyển mới
     */
    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * Thiết lập chiều rộng mới cho paddle.
     * 
     * @param width Chiều rộng mới
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Thiết lập chiều cao mới cho paddle.
     * 
     * @param height Chiều cao mới
     */
    public void setHeight(int height) {
        this.height = height;
    }
}