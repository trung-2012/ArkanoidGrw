package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * WoodBrick - Gạch gỗ
 * Health: 2, Points: 20
 */
public class WoodBrick extends Brick {
    private static Image brickImage;
    
    static {
        try {
            brickImage = new Image(WoodBrick.class.getResourceAsStream("/game/arkanoid/images/BrickWood.png"));
        } catch (Exception e) {
            System.err.println("Cannot load WoodBrick image: " + e.getMessage());
        }
    }
    
    public WoodBrick(Vector2D position) {
        super(position, 2, 20); // 2 health, 20 points
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
