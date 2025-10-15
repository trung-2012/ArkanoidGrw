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

    // Movement methods
    public void moveLeft() {
        // Move paddle left by moveSpeed
        this.position.setX(this.position.getX() - this.moveSpeed);
        // Clamp to left edge (position is center)
        double half = this.width / 2.0;
        if (this.position.getX() - half < 0)
            this.position.setX(half);
    }

    public void moveRight() {
        // Move paddle right by moveSpeed
        this.position.setX(this.position.getX() + this.moveSpeed);
    }

    // Update could include friction/animations; keep placeholder to match API
    public void update() {
        // currently no-op; movement is applied directly in moveLeft/moveRight
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