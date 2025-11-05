package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * InsaneBrick - Gạch siêu cứng
 * Health: 100, Points: 1000
 * Hầu như không thể phá hủy!
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
    
    @Override
    protected void onDamage() {
        super.onDamage();
    }
}
