package game.arkanoid.models;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class PowerUp {
    private double x, y;
    private double speed = 1.0;
    private double size = 24;
    private PowerUpType type;
    private Image image;

    public PowerUp(double x, double y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        try {
            String path = "/game/arkanoid/images/" + type.name().toLowerCase() + ".png";
            this.image = new Image(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            this.image = null; // fallback nếu không có ảnh
        }
    }

    public void update() {
        y += speed;
    }

    public boolean intersects(Paddle paddle) {
        Rectangle2D rect1 = new Rectangle2D(x - size / 2, y - size / 2, size, size);
        Rectangle2D rect2 = new Rectangle2D(paddle.getPosition().getX() - paddle.getWidth() / 2,
                paddle.getPosition().getY() - paddle.getHeight() / 2,
                paddle.getWidth(), paddle.getHeight());
        return rect1.intersects(rect2);
    }

    // Getter
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public Image getImage() { return image; }
    public PowerUpType getType() { return type; }
}
