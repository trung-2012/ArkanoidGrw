package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LaserBeam {
    private Vector2D position;
    private double speed = 12;
    private double width = 3;
    private double height = 5;

    public LaserBeam(Vector2D position) {
        this.position = position;
    }

    public void update() {
        position.setY(position.getY() - speed);
    }

    public boolean isOffScreen(double canvasHeight) {
        return position.getY() + height < 0;
    }

    public void render(GraphicsContext gc) {
        // Vẽ tia laser đỏ sáng có hiệu ứng glow
        gc.save();
        gc.setGlobalAlpha(0.9);
        gc.setFill(Color.rgb(0, 215, 240));
        gc.fillRect(position.getX() - width / 2, position.getY() - height, width, height * 2);

        // Hiệu ứng sáng quanh tia
        gc.setGlobalAlpha(0.4);
        gc.setFill(Color.rgb(153, 255, 255, 0.6));
        gc.fillRect(position.getX() - width * 2, position.getY() - height * 2, width * 4, height * 4);
        gc.restore();
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
