package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * ExplodeBrick - Gạch nổ
 * Health: 2, Points: 30
 * Khi bị phá hủy sẽ trigger explosion effect
 */
public class ExplodeBrick extends Brick {
    private static Image brickImage;
    private ExplosionHandler explosionHandler;
    
    static {
        try {
            brickImage = new Image(ExplodeBrick.class.getResourceAsStream("/game/arkanoid/images/ExplodeBrick.png"));
        } catch (Exception e) {
            System.err.println("Cannot load ExplodeBrick image: " + e.getMessage());
        }
    }
    
    public ExplodeBrick(Vector2D position) {
        super(position, 2, 30); // 2 health, 30 points
    }
    
    // Setter cho explosion handler
    public void setExplosionHandler(ExplosionHandler handler) {
        this.explosionHandler = handler;
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
    protected void onDestroyed() {
        super.onDestroyed();
        // Trigger explosion effect thông qua callback
        if (explosionHandler != null) {
            explosionHandler.handleExplosion(this);
        }
    }

    // Functional interface cho explosion callback
    @FunctionalInterface
    public interface ExplosionHandler {
        void handleExplosion(ExplodeBrick brick);
    }
}
