package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;


public class LaserBeam extends GameObject {
    private static final double DEFAULT_WIDTH = 15;
    private static final double DEFAULT_HEIGHT = 30;
    private static final double DEFAULT_SPEED = 12;
    
    private double speed;
    private Image bulletImage;

    public LaserBeam(Vector2D position, Image bulletImage) {
        super(position, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.speed = DEFAULT_SPEED;
        this.bulletImage = bulletImage;
    }

    @Override
    public void update() {
        position.setY(position.getY() - speed);
    }

    @Override
    public void render(GraphicsContext gc) {
        // Vẽ tia laser bằng ảnh nếu có, nếu không thì dùng màu mặc định
        if (bulletImage != null) {
            gc.drawImage(bulletImage, 
                position.getX() - width / 2, 
                position.getY() - height / 2, 
                width, 
                height);
        } else {
            // Fallback: vẽ tia laser nếu không có ảnh
            gc.setFill(Color.rgb(0, 215, 240));
            gc.fillRect(position.getX() - width / 2, position.getY() - height / 2, width, height);
        }
    }

    public boolean isOffScreen(double canvasHeight) {
        return position.getY() + height < 0;
    }

    public boolean intersects(Brick brick) {
        double bx = brick.getPosition().getX();
        double by = brick.getPosition().getY();

        return position.getX() + width / 2 >= bx &&
                position.getX() - width / 2 <= bx + GameConstants.BRICK_WIDTH &&
                position.getY() >= by &&
                position.getY() - height <= by + GameConstants.BRICK_HEIGHT;
    }
}
