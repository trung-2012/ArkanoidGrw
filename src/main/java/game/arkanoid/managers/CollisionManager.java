package game.arkanoid.managers;

import game.arkanoid.models.*;
import game.arkanoid.utils.GameConstants;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

import java.util.List;

/**
 * CollisionManager quản lý tất cả các va chạm trong game.
 * Áp dụng Manager Pattern và Observer Pattern (callbacks).
 * Xử lý collision giữa: Ball-Wall, Ball-Paddle, Ball-Brick, Ball-Shield, Laser-Brick, PowerUp-Paddle.
 *
 * @author ArkanoidGrw
 * @version 1.0
 */
public class CollisionManager {

    // đa bóng
    private List<Ball> balls;
    private Paddle paddle;

    /** Danh sách các viên gạch */
    private List<Brick> bricks;

    /** Tấm khiên bảo vệ (nếu có) */
    private Shield shield;

    /** Canvas để lấy kích thước màn hình */
    private Canvas canvas;

    /** kiểm tra bóng dính vào paddle*/
    private boolean ballAttachedToPaddle = false;

    /** Callback khi bóng rơi ra ngoài (mất mạng) */
    private CollisionCallback onBallFallOut;

    /** Callback khi gạch bị phá hủy (với data) */
    private CollisionCallbackWithData onBrickDestroyed;

    /** Callback khi hoàn thành level */
    private CollisionCallback onLevelComplete;

    /** Callback cho âm thanh*/
    private CollisionCallback onPaddleHit;

    /**
     * Constructor khởi tạo CollisionManager.
     *
     * @param ball Quả bóng
     * @param paddle Thanh đỡ
     * @param bricks Danh sách gạch
     * @param canvas Canvas game
     */
    public CollisionManager(List<Ball> balls, Paddle paddle, List<Brick> bricks, Canvas canvas) {
        this.balls = balls;
        this.paddle = paddle;
        this.bricks = bricks;
        this.canvas = canvas;
    }

    /**
     * Kiểm tra tất cả các va chạm trong game mỗi frame.
     * Gọi các sub-methods để check từng loại collision riêng biệt.
     */
    public void checkAllCollisions() {
        if (balls == null || balls.isEmpty()) return;

        double screenWidth = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        double screenHeight = (canvas != null) ? canvas.getHeight() : GameConstants.WINDOW_HEIGHT;

        checkBallWallCollision(screenWidth, screenHeight);
        checkBallPaddleCollision();
        checkBallsShieldCollision();
        checkBallsBricksCollision();
    }

    /**
     * Kiểm tra va chạm giữa bóng và tường.
     * Nếu bóng rơi ra ngoài đáy, trigger callback onBallFallOut.
     *
     * @param screenWidth Chiều rộng màn hình
     * @param screenHeight Chiều cao màn hình
     */
    private void checkBallWallCollision(double screenWidth, double screenHeight) {

        if (balls == null) return;

        // Duyệt từng bóng
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);

            boolean fallOut = b.collideWithWall(screenWidth, screenHeight);

            if (fallOut) {
                // Nếu còn nhiều bóng → chỉ xóa bóng rơi
                if (balls.size() > 1) {
                    balls.remove(i);
                    i--;
                    continue;
                }

                // Nếu đây là bóng cuối cùng → mất mạng
                if (onBallFallOut != null) {
                    onBallFallOut.onCollision();
                }
            }
        }
    }

    /**
     * Kiểm tra va chạm giữa bóng và paddle.
     * Bóng sẽ nảy lại khi chạm paddle.
     */
    private void checkBallPaddleCollision() {
        if (paddle == null || this.ballAttachedToPaddle) return;

        for (Ball b : balls) {
            if(b.collideWith(paddle)) {
                if(onPaddleHit != null) {
                    onPaddleHit.onCollision();
                }
            }
        }
    }

    /**
     * Kiểm tra va chạm giữa bóng và shield (tấm khiên).
     * Shield sẽ bị damage và bóng nảy ngược lại.
     */
    private void checkBallsShieldCollision() {
        if (shield == null) return;

        long now = System.currentTimeMillis();

        for (Ball b : balls) {

            // chống spam bounce
            if (now < b.nextShieldBounceAllowed)
                continue;

            if (shield.collidesWith(b)) {

                // bounce an toàn
                b.reverseVelocityY();

                //cooldown 120ms để tránh giật
                b.nextShieldBounceAllowed = now + 120;

                // shield mất HP
                shield.hit();

                if (shield.isBroken()) {
                    shield = null;
                    break;
                }
            }
        }
    }

    /**
     * Kiểm tra va chạm giữa bóng và các gạch.
     * Khi gạch bị phá hủy, trigger callback onBrickDestroyed.
     * Nếu hết gạch, trigger callback onLevelComplete.
     */
    private void checkBallsBricksCollision() {
        if (bricks == null) return;

        for (Ball b : balls) {
            checkSingleBallBrickCollision(b);
        }
    }

    private void checkSingleBallBrickCollision(Ball ball) {
        for (Brick brick : bricks) {

            if (brick.isDestroyed()) continue;

            if (ball.collideWith(brick)) {

                // Check xem còn gạch nào không
                boolean anyLeft = checkAnyBricksLeft();

                // Nếu gạch bị phá hủy sau khi nhận damage
                if (brick.isDestroyed() && onBrickDestroyed != null) {
                    BrickCollisionData data = new BrickCollisionData(brick, anyLeft);
                    onBrickDestroyed.onCollision(data);
                }

                // Check level completion
                if (!anyLeft && onLevelComplete != null) {
                    Platform.runLater(() -> onLevelComplete.onCollision());
                }

                break;  // Chỉ xử lý 1 collision per frame
            }
        }
    }

    /**
     * Kiểm tra va chạm giữa laser beam và gạch.
     *
     * @param beam Laser beam cần kiểm tra
     * @return true nếu laser trúng gạch, false nếu không
     */
    public boolean checkLaserBrickCollision(LaserBeam beam) {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && beam.intersects(brick)) {
                brick.takeDamage();
                boolean anyLeft = checkAnyBricksLeft();
                if (brick.isDestroyed() && onBrickDestroyed != null) {
                    BrickCollisionData data = new BrickCollisionData(brick, anyLeft);
                    onBrickDestroyed.onCollision(data);

                    // Check level completion
                    if (!anyLeft && onLevelComplete != null) {
                        Platform.runLater(() -> onLevelComplete.onCollision());
                    }
                }

                return true; // Hit detected
            }
        }

        return false; // No hit
    }

    /**
     * Kiểm tra va chạm giữa power-up và paddle.
     *
     * @param powerUp Power-up cần kiểm tra
     * @return true nếu power-up được thu thập, false nếu không
     */
    public boolean checkPowerUpPaddleCollision(PowerUp powerUp) {
        if (paddle != null && powerUp.intersects(paddle)) {
            return true; // Collected
        }
        return false;
    }

    /**
     * Helper method kiểm tra còn gạch nào chưa bị phá không.
     *
     * @return true nếu còn gạch, false nếu hết gạch
     */
    private boolean checkAnyBricksLeft() {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    // Getters & Setters

    /**
     * Thiết lập ball mới.
     * @param ball Ball mới
     */
    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    /**
     * Thiết lập paddle mới.
     * @param paddle Paddle mới
     */
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    /**
     * Thiết lập danh sách bricks mới.
     * @param bricks Danh sách bricks mới
     */
    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }

    /**
     * Thiết lập shield mới.
     * @param shield Shield mới
     */
    public void setShield(Shield shield) {
        this.shield = shield;
    }

    /**
     * Lấy shield hiện tại.
     * @return Shield đang active (có thể null)
     */
    public Shield getShield() {
        return shield;
    }

    /**
     * Thiết lập canvas mới.
     * @param canvas Canvas mới
     */

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    // Callback Setters

    /**
     * Thiết lập callback khi bóng rơi ra ngoài.
     * @param callback Callback function
     */
    public void setOnBallFallOut(CollisionCallback callback) {
        this.onBallFallOut = callback;
    }

    /**
     * Thiết lập callback khi gạch bị phá hủy.
     * @param callback Callback function với data
     */
    public void setOnBrickDestroyed(CollisionCallbackWithData callback) {
        this.onBrickDestroyed = callback;
    }

    /**
     * Thiết lập callback khi hoàn thành level.
     * @param callback Callback function
     */
    public void setOnLevelComplete(CollisionCallback callback) {
        this.onLevelComplete = callback;
    }

    public void setOnPaddleHit(CollisionCallback callback) { this.onPaddleHit = callback; }

    public void setBallAttached(boolean attached) {
        this.ballAttachedToPaddle = attached;
    }

    // Callback Interfaces

    /**
     * Functional interface cho collision callback không có tham số.
     * Áp dụng Observer Pattern.
     */
    @FunctionalInterface
    public interface CollisionCallback {
        /**
         * Được gọi khi collision xảy ra.
         */
        void onCollision();
    }

    /**
     * Functional interface cho collision callback có data parameter.
     * Áp dụng Observer Pattern.
     */
    @FunctionalInterface
    public interface CollisionCallbackWithData {
        /**
         * Được gọi khi collision xảy ra với data.
         * @param data Dữ liệu collision
         */
        void onCollision(Object data);
    }

    /**
     * Data class chứa thông tin brick collision.
     * Sử dụng để truyền data cho callback.
     */
    public static class BrickCollisionData {
        /** Gạch bị phá hủy */
        public final Brick brick;

        /** Còn gạch nào không */
        public final boolean anyBricksLeft;

        /**
         * Constructor cho BrickCollisionData.
         * @param brick Gạch bị phá hủy
         * @param anyBricksLeft Còn gạch hay không
         */
        public BrickCollisionData(Brick brick, boolean anyBricksLeft) {
            this.brick = brick;
            this.anyBricksLeft = anyBricksLeft;
        }
    }
}
