package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import static game.arkanoid.utils.GameConstants.PADDLE_WIDTH;
import static game.arkanoid.utils.GameConstants.PADDLE_HEIGHT;
import static game.arkanoid.utils.GameConstants.PADDLE_SPEED;

public class Paddle {
    private Vector2D position;
    private int width;
    private int height;
    private double moveSpeed;

    // Constructor
    public Paddle(Vector2D position) {
        this.position = position;
        this.width = PADDLE_WIDTH;
        this.height = PADDLE_HEIGHT;
        this.moveSpeed = PADDLE_SPEED;
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

    public Vector2D getPosition() {
        return position;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
}