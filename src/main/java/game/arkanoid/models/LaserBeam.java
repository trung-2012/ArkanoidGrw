package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class LaserBeam {
    private Vector2D position;
    private double speed = 12;
    private double width = 15;
    private double height = 30;
    private Image bulletImage;

    public LaserBeam(Vector2D position, Image bulletImage) {
        this.position = position;
        this.bulletImage = bulletImage;
    }

    public void update() {
        position.setY(position.getY() - speed);
    }

    public boolean isOffScreen(double canvasHeight) {
        return position.getY() + height < 0;
    }

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

    public Vector2D getPosition() {
        return position;
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
