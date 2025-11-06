package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.image.Image;
public abstract class Brick extends GameObject {
    protected int health;
    protected int points;

    /**
     * Constructor cho Brick
     * @param position Vị trí góc trên trái của gạch
     * @param health Số máu của gạch
     * @param points Điểm số khi phá hủy gạch
     */
    public Brick(Vector2D position, int health, int points) {
        super(position, GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT);
        this.health = health;
        this.points = points;
    }

    @Override
    public void update() {
        // Brick không cần update logic
    }

    @Override
    public abstract void render(GraphicsContext gc);

    public abstract Image getBrickImage();

    public void takeDamage() {
        if (!active)
            return;
        this.health--;
        onDamage();
        if (this.health <= 0) {
            this.active = false;
            onDestroyed();
        }
    }

    // Hook method - gọi khi bị damage
    protected void onDamage() {
    }

    // Hook method - gọi khi bị phá hủy
    protected void onDestroyed() {
    }

    // Getters & Setters

    public boolean isDestroyed() {
        return !active;
    }

    public int getPoints() {
        return points;
    }

    @Deprecated
    public int getPoint() {
        return points;
    }

    public int getHealth() {
        return health;
    }

    public boolean getDestroyed() {
        return !active;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDestroyed(boolean destroyed) {
        this.active = !destroyed;
    }

    @Deprecated
    public BrickType getType() {
        return null;
    }

    @Deprecated
    public void setType(BrickType type) {
    }
}
