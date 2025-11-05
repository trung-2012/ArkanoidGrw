package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

import static game.arkanoid.utils.GameConstants.BALL_SPEED;

public class Ball extends GameObject {
    private static final int TRAIL_MAX = 20; // số điểm lưu lại
    private Vector2D velocity;
    private double radius;
    // Lưu dấu vết quỹ đạo bóng
    private final List<Vector2D> trail = new java.util.LinkedList<>();

    // Constructor
    public Ball(Vector2D position, double radius) {
        super(position, radius * 2, radius * 2);
        this.radius = radius;
        // Speed ban đầu chéo lên trên bên phải
        double diag = BALL_SPEED / Math.sqrt(2.0);
        this.velocity = new Vector2D(diag, -diag);
    }

    // Cập nhật vị trí bóng dựa trên vận tốc và lưu trail
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

    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi GameEngine với ballImage
    }

    // Đảo chiều các thành phần vận tốc
    public void reverseVelocityX() {
        this.velocity.setX(-this.velocity.getX());
    }

    public void reverseVelocityY() {
        this.velocity.setY(-this.velocity.getY());
    }

    // Phát hiện va chạm với tường (trả về true khi bóng rơi xuống dưới đáy)
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

    // Va chạm hình tròn-chữ nhật với paddle. Giả định paddle.position là tâm của paddle.
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

    // Va chạm hình tròn-chữ nhật với gạch. Giả định brick.position là góc trên trái của gạch.
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

    // Giúp hàm va chạm hình tròn-chữ nhật
    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public List<Vector2D> getTrail() {
        return trail;
    }

    // Getters & Setters

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }
}
