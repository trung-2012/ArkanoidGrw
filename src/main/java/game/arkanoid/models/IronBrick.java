package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * IronBrick - Gạch sắt
 * Health: 3, Points: 40
 */
public class IronBrick extends Brick {
    private static Image brickImage;
    
    static {
        try {
            brickImage = new Image(IronBrick.class.getResourceAsStream("/game/arkanoid/images/BrickIron.png"));
        } catch (Exception e) {
            System.err.println("Cannot load IronBrick image: " + e.getMessage());
        }
    }
    
    public IronBrick(Vector2D position) {
        super(position, 3, 40); // 3 health, 40 points
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
