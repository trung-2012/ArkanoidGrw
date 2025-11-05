package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import static game.arkanoid.utils.GameConstants.PADDLE_WIDTH;
import static game.arkanoid.utils.GameConstants.PADDLE_HEIGHT;
import static game.arkanoid.utils.GameConstants.PADDLE_SPEED;

public class Paddle extends GameObject {
    private double moveSpeed;

    // Constructor
    public Paddle(Vector2D position) {
        super(position, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.moveSpeed = PADDLE_SPEED;
    }

    @Override
    public void update() {
        // Paddle không tự động update
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