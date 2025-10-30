package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;

public class Brick {
    private BrickType type;
    private int health;
    private Vector2D position;
    private boolean destroyed;

    // Constructor
    public Brick(BrickType type, Vector2D position) {
        this.type = type;
        this.health = type.getHealth();
        this.position = position;
        this.destroyed = false;
    }

    // Xử lý sát thương và phá hủy
    public void takeDamage() {
        if (destroyed)
            return;
        this.health--;
        if (this.health <= 0) {
            this.destroyed = true;
        }
    }

    // Kiểm tra xem gạch đã bị phá hủy chưa
    public boolean isDestroyed() {
        return destroyed;
    }

    // Getters & Setters

    public BrickType getType() {
        return type;
    }

    public int getPoint() {
        return type.getPoint();
    }

    public int getHealth() {
        return health;
    }

    public Vector2D getPosition() {
        return position;
    }

    public boolean getDestroyed() {
        return destroyed;
    }

    public void setType(BrickType type) {
        this.type = type;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
