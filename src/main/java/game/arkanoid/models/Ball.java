package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

import static game.arkanoid.utils.GameConstants.BALL_SPEED;

/**
 * Lớp Ball đại diện cho quả bóng trong game Arkanoid.
 * Kế thừa từ GameObject và chứa logic di chuyển, va chạm với paddle/brick/wall.
 * Bóng có trail effect (hiệu ứng vệt đuôi) khi di chuyển.
 * 
 * @author ArkanoidGrw
 * @version 1.0
 */
public class Ball extends GameObject {
    /** Số lượng điểm tối đa trong trail effect */
    private static final int TRAIL_MAX = 20;
    
    /** Vector vận tốc của bóng (velocity) */
    private Vector2D velocity;
    
    /** Bán kính của bóng */
    private double radius;
    
    /** Danh sách các vị trí trước đó để tạo trail effect */
    private final List<Vector2D> trail = new java.util.LinkedList<>();

    private boolean original = true;

    public boolean isOriginal() {
        return original;
    }
    public void setOriginal(boolean value) {
        this.original = value;
    }
    public long nextShieldBounceAllowed = 0;

    /**
     * Constructor khởi tạo Ball với vị trí và bán kính.
     * Vận tốc ban đầu hướng chéo lên trên bên phải.
     * 
     * @param position Vị trí trung tâm của bóng
     * @param radius Bán kính của bóng
     */
    public Ball(Vector2D position, double radius) {
        super(position, radius * 2, radius * 2);
        this.radius = radius;
        // Speed ban đầu chéo lên trên bên phải
        double diag = BALL_SPEED / Math.sqrt(2.0);
        this.velocity = new Vector2D(diag, -diag);
    }

    /**
     * Cập nhật vị trí bóng dựa trên velocity và lưu trail.
     * Được gọi mỗi frame bởi game loop.
     */
    @Override
    public void update() {
        position.add(velocity);
        // Thêm vị trí hiện tại vào trail
        trail.add(new Vector2D(position.getX(), position.getY()));
        // Giữ số lượng điểm trail cố định
        if (trail.size() > TRAIL_MAX) {
            trail.remove(0);
        }
    }

    /**
     * Render bóng lên canvas.
     * Logic render được xử lý bởi RenderManager với ballImage.
     * 
     * @param gc GraphicsContext để vẽ
     */
    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi GameEngine với ballImage
    }

    /**
     * Đảo chiều vận tốc theo trục X (nảy trái/phải).
     */
    public void reverseVelocityX() {
        this.velocity.setX(-this.velocity.getX());
    }

    /**
     * Đảo chiều vận tốc theo trục Y (nảy lên/xuống).
     */
    public void reverseVelocityY() {
        this.velocity.setY(-this.velocity.getY());
    }

    /**
     * Phát hiện va chạm với tường và xử lý bounce.
     * Kiểm tra 4 cạnh: trái, phải, trên, dưới.
     * 
     * @param screenWidth Chiều rộng màn hình game
     * @param screenHeight Chiều cao màn hình game
     * @return true nếu bóng rơi xuống đáy (mất mạng), false nếu không
     */
    public boolean collideWithWall(double screenWidth, double screenHeight) {
        boolean bottomHit = false;

        // Tường bên trái
        if (position.getX() - radius <= 0) {
            position.setX(radius);
            reverseVelocityX();
        }

        // Tường bên phải
        if (position.getX() + radius >= screenWidth) {
            position.setX(screenWidth - radius);
            reverseVelocityX();
        }

        // Tường trên
        if (position.getY() - radius <= 0) {
            position.setY(radius);
            reverseVelocityY();
        }

        // Đáy (paddle bỏ lỡ) -> báo hiệu mất mạng / bóng rơi
        if (position.getY() - radius > screenHeight) {
            bottomHit = true;
        }

        return bottomHit;
    }

    /**
     * Va chạm hình tròn-chữ nhật với paddle.
     * Sử dụng thuật toán circle-rectangle collision detection.
     * Khi va chạm, bóng sẽ nảy ngược lại và có thể thay đổi hướng theo vị trí chạm paddle.
     * 
     * @param paddle Paddle cần kiểm tra va chạm
     * @return true nếu có va chạm, false nếu không
     */
    public boolean collideWith(Paddle paddle) {
        if (paddle == null)
            return false;

        double rx = paddle.getPosition().getX();
        double ry = paddle.getPosition().getY();
        double hw = paddle.getWidth() / 2.0;
        double hh = paddle.getHeight() / 2.0;

        double closestX = clamp(position.getX(), rx - hw, rx + hw);
        double closestY = clamp(position.getY(), ry - hh, ry + hh);

        double dx = position.getX() - closestX;
        double dy = position.getY() - closestY;

        double distanceSq = dx * dx + dy * dy;
        if (distanceSq <= radius * radius) {
            // Đẩy bóng ra khỏi paddle và phản xạ vận tốc Y
            position.setY(closestY - radius - 1);
            reverseVelocityY();

            // Điều chỉnh vận tốc X dựa trên vị trí va chạm dọc paddle
            double hitPos = (position.getX() - rx) / hw; // -1 .. 1
            double speed = Math.max(velocity.magnitude(), BALL_SPEED);
            double newVx = speed * hitPos * 0.8;
            this.velocity.setX(newVx);
            double vy = -Math.abs(Math.sqrt(Math.max(0, speed * speed - newVx * newVx)));
            this.velocity.setY(vy);

            return true;
        }

        return false;
    }

    /**
     * Va chạm hình tròn-chữ nhật với brick (gạch).
     * Sử dụng thuật toán circle-rectangle collision.
     * Khi va chạm, bóng nảy theo hướng phù hợp và gạch bị gây sát thương.
     * 
     * @param brick Brick cần kiểm tra va chạm
     * @return true nếu có va chạm, false nếu không
     */
    public boolean collideWith(Brick brick) {
        if (brick == null || brick.getDestroyed())
            return false;

        double bx = brick.getPosition().getX();
        double by = brick.getPosition().getY();
        double bw = GameConstants.BRICK_WIDTH;
        double bh = GameConstants.BRICK_HEIGHT;

        double closestX = clamp(position.getX(), bx, bx + bw);
        double closestY = clamp(position.getY(), by, by + bh);

        double dx = position.getX() - closestX;
        double dy = position.getY() - closestY;

        double distanceSq = dx * dx + dy * dy;
        if (distanceSq <= radius * radius) {
            // Quyết định phản xạ X hay Y dựa trên độ xuyên vào
            double overlapLeft = Math.abs(position.getX() - bx);
            double overlapRight = Math.abs(position.getX() - (bx + bw));
            double overlapTop = Math.abs(position.getY() - by);
            double overlapBottom = Math.abs(position.getY() - (by + bh));

            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
            if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                reverseVelocityX();
            } else {
                reverseVelocityY();
            }

            // Gây sát thương cho gạch
            brick.takeDamage();
            return true;
        }

        return false;
    }

    /**
     * Helper method giới hạn giá trị trong khoảng [min, max].
     * Được sử dụng trong thuật toán collision detection.
     * 
     * @param val Giá trị cần clamp
     * @param min Giá trị tối thiểu
     * @param max Giá trị tối đa
     * @return Giá trị đã được clamp
     */
    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Lấy danh sách các điểm trong trail (vệt đuôi).
     * 
     * @return List các Vector2D chứa vị trí trước đó của bóng
     */
    public List<Vector2D> getTrail() {
        return trail;
    }

    // ==================== Getters & Setters ====================

    /**
     * Lấy bán kính của bóng.
     * 
     * @return Bán kính (pixels)
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Thiết lập bán kính mới cho bóng.
     * 
     * @param radius Bán kính mới (pixels)
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Lấy vector vận tốc của bóng.
     * 
     * @return Vector2D chứa vận tốc (vx, vy)
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * Thiết lập vận tốc mới cho bóng.
     * 
     * @param velocity Vector2D chứa vận tốc mới (vx, vy)
     */
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }
}
