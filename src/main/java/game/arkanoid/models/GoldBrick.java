package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * GoldBrick - Gạch vàng
 * Health: 4, Points: 50
 */
public class GoldBrick extends Brick {
    private static Image brickImage;
    
    static {
        try {
            brickImage = new Image(GoldBrick.class.getResourceAsStream("/game/arkanoid/images/BrickGold.png"));
        } catch (Exception e) {
            System.err.println("Cannot load GoldBrick image: " + e.getMessage());
        }
    }
    
    public GoldBrick(Vector2D position) {
        super(position, 4, 50); // 4 health, 50 points
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
}
