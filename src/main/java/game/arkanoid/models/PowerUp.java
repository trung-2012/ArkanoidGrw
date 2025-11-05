package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PowerUp extends GameObject {
    private static final double DEFAULT_SIZE = 24;
    private static final double DEFAULT_SPEED = 1.0;
    
    private double speed;
    private PowerUpType type;
    private Image image;

    public PowerUp(double x, double y, PowerUpType type) {
        super(new Vector2D(x, y), DEFAULT_SIZE, DEFAULT_SIZE);
        this.speed = DEFAULT_SPEED;
        this.type = type;
        try {
            String path = "/game/arkanoid/images/" + type.name().toLowerCase() + ".png";
            this.image = new Image(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            this.image = null; // fallback nếu không có ảnh
        }
    }

    @Override
    public void update() {
        position.setY(position.getY() + speed);
    }

    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi GameEngine
    }

    public boolean intersects(Paddle paddle) {
        Rectangle2D rect1 = new Rectangle2D(
            position.getX() - width / 2, 
            position.getY() - height / 2, 
            width, 
            height
        );
        Rectangle2D rect2 = new Rectangle2D(
            paddle.getPosition().getX() - paddle.getWidth() / 2,
            paddle.getPosition().getY() - paddle.getHeight() / 2,
            paddle.getWidth(), 
            paddle.getHeight()
        );
        return rect1.intersects(rect2);
    }

    // Getters

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getSize() {
        return width; // size = width = height
    }

    public Image getImage() {
        return image;
    }

    public PowerUpType getType() {
        return type;
    }
}
