package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * NormalBrick - Gạch thường
 * Health: 1, Points: 10
 */
public class NormalBrick extends Brick {
    private static Image brickImage;
    
    static {
        try {
            brickImage = new Image(NormalBrick.class.getResourceAsStream("/game/arkanoid/images/BrickNormal.png"));
        } catch (Exception e) {
            System.err.println("Cannot load NormalBrick image: " + e.getMessage());
        }
    }
    
    public NormalBrick(Vector2D position) {
        super(position, 1, 10); // 1 health, 10 points
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
