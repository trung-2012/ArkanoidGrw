package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DebrisEffect - Hiệu ứng mảnh vụn rơi khi gạch bị phá hủy.
 * Các mảnh vụn sẽ rơi xuống dưới với trọng lực và độ trong suốt giảm dần.
 */
public class DebrisEffect extends GameObject {
    private static final int DEBRIS_COUNT = 30;
    private static final double GRAVITY = 0.5;
    private static final double INITIAL_VELOCITY_RANGE = 5.0;
    private static final int LIFETIME = 60;
    
    private final List<Debris> debrisList;
    private int frameCounter;
    private boolean finished;
    private final Color debrisColor;
    
    /**
     * Constructor tạo debris effect tại vị trí gạch bị phá hủy.
     * 
     * @param position Vị trí trung tâm của gạch
     * @param brickColor Màu sắc của gạch (để tạo mảnh vụn cùng màu)
     */
    public DebrisEffect(Vector2D position, Color brickColor) {
        super(position, 0, 0);
        this.debrisColor = brickColor != null ? brickColor : Color.GRAY;
        this.debrisList = new ArrayList<>();
        this.frameCounter = 0;
        this.finished = false;
        
        initDebris(position);
    }
    
    /**
     * Khởi tạo các mảnh vụn với vị trí và vận tốc ngẫu nhiên.
     */
    private void initDebris(Vector2D center) {
        Random random = new Random();
        
        for (int i = 0; i < DEBRIS_COUNT; i++) {
            double angle = (Math.PI * 2 * i) / DEBRIS_COUNT;
            double speed = random.nextDouble() * INITIAL_VELOCITY_RANGE + 2.0;
            
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            double size = random.nextDouble() * 4 + 2;
            
            debrisList.add(new Debris(
                new Vector2D(center.getX(), center.getY()),
                new Vector2D(vx, vy),
                size
            ));
        }
    }
    
    @Override
    public void update() {
        if (finished) return;
        
        frameCounter++;
        
        // Update từng mảnh vụn
        for (Debris debris : debrisList) {
            debris.update();
        }
        
        // Kết thúc khi hết lifetime
        if (frameCounter >= LIFETIME) {
            finished = true;
            this.active = false;
        }
    }
    
    @Override
    public void render(GraphicsContext gc) {
        if (finished) return;
        
        // Tính độ trong suốt giảm dần theo thời gian
        double opacity = 1.0 - ((double) frameCounter / LIFETIME);
        
        gc.setFill(Color.color(
            debrisColor.getRed(),
            debrisColor.getGreen(),
            debrisColor.getBlue(),
            opacity
        ));
        
        // Vẽ từng mảnh vụn
        for (Debris debris : debrisList) {
            debris.render(gc);
        }
        
        // Reset alpha về 1.0
        gc.setGlobalAlpha(1.0);
    }
    
    /**
     * Kiểm tra xem effect đã kết thúc chưa.
     * 
     * @return true nếu đã kết thúc, false nếu chưa
     */
    public boolean isFinished() {
        return finished || !active;
    }
    
    /**
     * Inner class đại diện cho một mảnh vụn.
     */
    private class Debris {
        private Vector2D position;
        private Vector2D velocity;
        private final double size;
        
        public Debris(Vector2D position, Vector2D velocity, double size) {
            this.position = position;
            this.velocity = velocity;
            this.size = size;
        }
        
        public void update() {
            // Áp dụng trọng lực
            velocity = new Vector2D(velocity.getX(), velocity.getY() + GRAVITY);
            
            // Cập nhật vị trí
            position = new Vector2D(
                position.getX() + velocity.getX(),
                position.getY() + velocity.getY()
            );
        }
        
        public void render(GraphicsContext gc) {
            gc.fillRect(position.getX(), position.getY(), size, size);
        }
    }
}
