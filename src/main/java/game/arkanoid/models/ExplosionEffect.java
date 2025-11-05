package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.GameConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ExplosionEffect {
    private Vector2D position;
    private int currentFrame;
    private int frameDelay;
    private int frameCounter;
    private boolean finished;
    private Image explosionImage;
    
    private static final int TOTAL_FRAMES = 5;  
    private static final int FRAME_SIZE = 31; 
    private static final int FRAME_SPACING = 32;
    private static final int FRAME_DELAY = 10; 
    
    public ExplosionEffect(Vector2D position, Image explosionImage) {
        this.position = position;
        this.explosionImage = explosionImage;
        this.currentFrame = TOTAL_FRAMES - 1;
        this.frameCounter = 0;
        this.frameDelay = FRAME_DELAY;
        this.finished = false;
    }
    
    public void update() {
        if (finished) return;
        
        frameCounter++;
        if (frameCounter >= frameDelay) {
            frameCounter = 0;
            currentFrame--;
            
            if (currentFrame < 0) {
                finished = true;
            }
        }
    }
    
    public void render(GraphicsContext gc) {
        if (finished || explosionImage == null) return;
        
        double sourceX = 0;
        double sourceY = currentFrame * FRAME_SPACING;
        
        // Vẽ frame với kích thước gạch
        gc.drawImage(explosionImage, 
            sourceX, sourceY, FRAME_SIZE, FRAME_SIZE, 
            position.getX(), position.getY(), 
            GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT); 
    }
    
    public boolean isFinished() {
        return finished;
    }
    
    public Vector2D getPosition() {
        return position;
    }
}
