package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {
    protected Vector2D position;
    protected double width;
    protected double height;
    protected boolean active; // Object có hđ hay k

    /**
     * Constructor cho GameObject
     * @param position Vị trí của object
     * @param width Chiều rộng
     * @param height Chiều cao
     */
    public GameObject(Vector2D position, double width, double height) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.active = true;
    }

    // Update logic cho object
    public abstract void update();

    // Render obj lên canvas
    public abstract void render(GraphicsContext gc);

    // Check va chạm với obj khác
    public boolean collidesWith(GameObject other) {
        if (other == null || !other.isActive() || !this.isActive()) {
            return false;
        }
        
        double thisLeft = position.getX() - width / 2;
        double thisRight = position.getX() + width / 2;
        double thisTop = position.getY() - height / 2;
        double thisBottom = position.getY() + height / 2;
        
        double otherLeft = other.position.getX() - other.width / 2;
        double otherRight = other.position.getX() + other.width / 2;
        double otherTop = other.position.getY() - other.height / 2;
        double otherBottom = other.position.getY() + other.height / 2;
        
        return thisLeft < otherRight && thisRight > otherLeft &&
               thisTop < otherBottom && thisBottom > otherTop;
    }

    // Xử lí khi va chạm với obj khác
    public void onCollision(GameObject other) {
        // Default: không làm gì
        // Subclass override để thêm logic riêng
    }

    // Kiểm tra object có nằm trong bounds không
    public boolean isOutOfBounds(double maxWidth, double maxHeight) {
        return position.getX() < 0 || position.getX() > maxWidth ||
               position.getY() < 0 || position.getY() > maxHeight;
    }

    // Getters & Setters

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Deactivate object
    public void deactivate() {
        this.active = false;
    }

    // Lấy center pos của object
    public Vector2D getCenter() {
        return new Vector2D(position.getX(), position.getY());
    }

    // Lấy bounds (left, top, right, bottom)
    public double[] getBounds() {
        return new double[] {
            position.getX() - width / 2,  // left
            position.getY() - height / 2, // top
            position.getX() + width / 2,  // right
            position.getY() + height / 2  // bottom
        };
    }
}
