package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.concurrent.ThreadLocalRandom;

/**
 * InsaneBrick - Gạch siêu cứng
 * Health: 100, Points: 1000
 * Có cơ chế đặc biệt: khi va chạm với ball hoặc laser, có 5%
 * khả năng bị vỡ ngay lập tức bất kể health còn lại.
 */
public class InsaneBrick extends Brick {
    private static Image brickImage;
    
    static {
        try {
            brickImage = new Image(InsaneBrick.class.getResourceAsStream("/game/arkanoid/images/BrickInsane.png"));
        } catch (Exception e) {
            System.err.println("Cannot load InsaneBrick image: " + e.getMessage());
        }
    }
    
    public InsaneBrick(Vector2D position) {
        super(position, 100, 1000); // 100 health, 1000 points
    }
    
    @Override
    public void render(GraphicsContext gc) {
        if (active && brickImage != null) {
            gc.drawImage(brickImage, position.getX(), position.getY(), width, height);
        }
    }
    
    @Override
    public Image getBrickImage() {
        return brickImage;
    }

    /**
     * Khi may mắn xảy ra (5%), gạch sẽ bị phá ngay lập tức và onDestroyed() được gọi.
     * Nếu không, hành vi mặc định (giảm health) được thực hiện.
     */
    @Override
    public void takeDamage() {
        if (!active) return;

        // 5% chance to instantly destroy the InsaneBrick
        double r = ThreadLocalRandom.current().nextDouble();
        if (r < 0.05) {
            // instant destroy regardless of remaining health
            this.health = 0;
            this.active = false;
            onDestroyed();
            return;
        }

        // Default behavior
        super.takeDamage();
    }
}
