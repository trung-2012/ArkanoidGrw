package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public class Brick extends GameObject {
    private BrickType type;
    private int health;

    // Constructor
    public Brick(BrickType type, Vector2D position) {
        super(position, GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT);
        this.type = type;
        this.health = type.getHealth();
        // active = true
    }

    @Override
    public void update() {
        // Static obj k cần update
    }

    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi GameEngine với brickImage
    }

    // Xử lý sát thương và phá hủy
    public void takeDamage() {
        if (!active)
            return;
        this.health--;
        if (this.health <= 0) {
            this.active = false;
        }
    }

    // Kiểm tra xem gạch đã bị phá hủy chưa
    public boolean isDestroyed() {
        return !active;
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

    public boolean getDestroyed() {
        return !active;
    }

    public void setType(BrickType type) {
        this.type = type;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDestroyed(boolean destroyed) {
        this.active = !destroyed;
    }
}
