package game.arkanoid.models;

import game.arkanoid.powerup.PowerUpType;
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

    /** Tốc độ hiện tại của bóng */
    private double currentSpeed = BALL_SPEED;
    /** Tốc độ gốc để reset sau khi power-up hết hạn */
    private double originalSpeed = BALL_SPEED;
    /** Loại power-up tốc độ đang active (null nếu không có) */
    private PowerUpType currentSpeedPowerUp = null;
    /** Thời điểm (ms) power-up tốc độ hết hạn */
    private long speedPowerUpEndTime = 0;

    /**
     * Thiết lập tốc độ hiện tại của bóng.
     * 
     * @param s Tốc độ mới
     */
    public void setCurrentSpeed(double s) {
        this.currentSpeed = s;
    }

    /**
     * Lấy tốc độ hiện tại của bóng.
     * 
     * @return Tốc độ hiện tại
     */
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * Kiểm tra xem bóng có phải là bóng gốc (không phải clone từ multi-ball).
     * 
     * @return true nếu là bóng gốc, false nếu là clone
     */
    public boolean isOriginal() {
        return original;
    }
    
    /**
     * Thiết lập trạng thái bóng gốc.
     * 
     * @param value true nếu là bóng gốc, false nếu là clone
     */
    public void setOriginal(boolean value) {
        this.original = value;
    }
    
    /**
     * Áp dụng hiệu ứng WEAK power-up (giảm tốc độ xuống MIN_BALL_SPEED).
     * Tự động hết hạn sau BALL_SPEED_POWERUP_DURATION ms.
     */
    public void applyWeakSpeed() {
        this.currentSpeed = GameConstants.MIN_BALL_SPEED;
        this.currentSpeedPowerUp = PowerUpType.WEAK;
        this.speedPowerUpEndTime = System.currentTimeMillis() + GameConstants.BALL_SPEED_POWERUP_DURATION;
        updateVelocityMagnitude();
    }
    
    /**
     * Áp dụng hiệu ứng STRONG power-up (tăng tốc độ lên MAX_BALL_SPEED).
     * Tự động hết hạn sau BALL_SPEED_POWERUP_DURATION ms.
     */
    public void applyStrongSpeed() {
        this.currentSpeed = GameConstants.MAX_BALL_SPEED;
        this.currentSpeedPowerUp = PowerUpType.STRONG;
        this.speedPowerUpEndTime = System.currentTimeMillis() + GameConstants.BALL_SPEED_POWERUP_DURATION;
        updateVelocityMagnitude();
    }
    
    /**
     * Reset tốc độ về giá trị gốc (originalSpeed).
     * Được gọi tự động khi power-up hết hạn.
     */
    private void resetSpeed() {
        this.currentSpeed = this.originalSpeed;
        this.currentSpeedPowerUp = null;
        this.speedPowerUpEndTime = 0;
        updateVelocityMagnitude();
    }
    
    /**
     * Cập nhật độ lớn (magnitude) của velocity vector dựa trên currentSpeed.
     * Giữ nguyên hướng nhưng thay đổi độ lớn.
     */
    private void updateVelocityMagnitude() {
        if (velocity == null) return;
        double len = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY());
        if (len == 0) return;
        double nx = velocity.getX() / len;
        double ny = velocity.getY() / len;
        velocity.setX(nx * currentSpeed);
        velocity.setY(ny * currentSpeed);
    }
    
    /** Thời điểm sớm nhất cho phép va chạm shield tiếp theo (tránh spam bounce) */
    public long nextShieldBounceAllowed = 0;
    
    /** Brick vừa va chạm gần nhất (để tránh double bounce với cùng 1 brick) */
    private Brick lastCollidedBrick = null;
    
    /** Thời điểm va chạm với brick gần nhất */
    private long lastBrickCollisionTime = 0;

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
        double diag = currentSpeed / Math.sqrt(2.0);
        this.velocity = new Vector2D(diag, -diag);
    }

    /**
     * Cập nhật vị trí bóng dựa trên velocity và lưu trail.
     * Được gọi mỗi frame bởi game loop.
     */
    @Override
    public void update() {
        // Kiểm tra hết hiệu lực power-up tốc độ
        if (currentSpeedPowerUp != null && System.currentTimeMillis() > speedPowerUpEndTime) {
            resetSpeed(); // Trở về tốc độ bình thường
        }
        
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
            double speed = currentSpeed;

            double newVx = speed * hitPos * 0.8;
            double newVy = -Math.abs(Math.sqrt(Math.max(0, speed * speed - newVx * newVx)));

            velocity.setX(newVx);
            velocity.setY(newVy);

            double len = Math.sqrt(newVx * newVx + newVy * newVy);
            if (len != 0) {
                velocity.setX((newVx / len) * currentSpeed);
                velocity.setY((newVy / len) * currentSpeed);
            }


            return true;
        }

        return false;
    }

    /**
     * Va chạm circle-rectangle với brick (gạch).
     * Sử dụng thuật toán tìm điểm gần nhất trên rectangle để detect collision chính xác.
     * Per-brick cooldown để tránh double bounce với cùng 1 brick.
     * 
     * @param brick Brick cần kiểm tra va chạm
     * @return true nếu có va chạm, false nếu không
     */
    public boolean collideWith(Brick brick) {
        if (brick == null || brick.getDestroyed())
            return false;
        
        // Cooldown per-brick: chỉ tránh va chạm lại với CÙNG brick trong 50ms
        long now = System.currentTimeMillis();
        if (lastCollidedBrick == brick && now - lastBrickCollisionTime < 50) {
            return false;
        }

        // Brick bounds (top-left corner)
        double brickLeft = brick.getPosition().getX();
        double brickTop = brick.getPosition().getY();
        double brickRight = brickLeft + GameConstants.BRICK_WIDTH;
        double brickBottom = brickTop + GameConstants.BRICK_HEIGHT;
        
        // Ball center
        double ballCx = position.getX();
        double ballCy = position.getY();
        
        // Tìm điểm gần nhất trên brick với ball center
        double nearestX = clamp(ballCx, brickLeft, brickRight);
        double nearestY = clamp(ballCy, brickTop, brickBottom);
        
        // Tính khoảng cách từ ball center đến điểm gần nhất
        double dx = ballCx - nearestX;
        double dy = ballCy - nearestY;
        double distanceSq = dx * dx + dy * dy;
        
        // Kiểm tra collision
        if (distanceSq > radius * radius) {
            return false; // Không va chạm
        }
        
        // Xử lý va chạm
        
        // Xác định hướng va chạm dựa vào vị trí ball so với brick
        boolean hitFromLeft = ballCx < brickLeft && velocity.getX() > 0;
        boolean hitFromRight = ballCx > brickRight && velocity.getX() < 0;
        boolean hitFromTop = ballCy < brickTop && velocity.getY() > 0;
        boolean hitFromBottom = ballCy > brickBottom && velocity.getY() < 0;
        
        // Đẩy bóng ra khỏi brick và reverse velocity
        if (hitFromLeft) {
            // Va chạm từ trái → Đẩy về trái
            position.setX(brickLeft - radius - 1);
            reverseVelocityX();
        } else if (hitFromRight) {
            // Va chạm từ phải → Đẩy về phải
            position.setX(brickRight + radius + 1);
            reverseVelocityX();
        } else if (hitFromTop) {
            // Va chạm từ trên → Đẩy lên trên
            position.setY(brickTop - radius - 1);
            reverseVelocityY();
        } else if (hitFromBottom) {
            // Va chạm từ dưới → Đẩy xuống dưới
            position.setY(brickBottom + radius + 1);
            reverseVelocityY();
        } else {
            // Va chạm từ góc → Xác định hướng dựa vào dx, dy
            if (Math.abs(dx) > Math.abs(dy)) {
                // Va chạm ngang chủ yếu
                position.setX(ballCx + (dx > 0 ? radius + 1 : -radius - 1));
                reverseVelocityX();
            } else {
                // Va chạm dọc chủ yếu
                position.setY(ballCy + (dy > 0 ? radius + 1 : -radius - 1));
                reverseVelocityY();
            }
        }
        
        // Đảm bảo bóng không bay ngang mãi (tránh kẹt trên không)
        double minVerticalSpeed = 1.0;
        if (Math.abs(velocity.getY()) < minVerticalSpeed) {
            velocity.setY(velocity.getY() > 0 ? minVerticalSpeed : -minVerticalSpeed);
        }
        
        // Cập nhật cooldown per-brick
        lastCollidedBrick = brick;
        lastBrickCollisionTime = now;
        
        // Gây sát thương
        brick.takeDamage();
        return true;
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

    // Getters & Setters

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
