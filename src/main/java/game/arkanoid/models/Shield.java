package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Lớp Shield đại diện cho tấm khiên bảo vệ paddle.
 * Shield có 3 mạng và sẽ glow (sáng lên) khi bị hit.
 * Kế thừa từ GameObject.
 * 
 * @author ArkanoidGrw
 * @version 1.0
 */
public class Shield extends GameObject {
    /** Số máu tối đa của shield */
    private static final int MAX_HEALTH = 3;
    
    /** Thời gian glow effect (milliseconds) */
    private static final long GLOW_DURATION = 150;
    
    /** Máu hiện tại của shield */
    private int health;
    
    /** Timer cho glow effect */
    private long glowTimer = 0;

    /**
     * Constructor khởi tạo Shield.
     * 
     * @param x Tọa độ X (góc trên trái)
     * @param y Tọa độ Y (góc trên trái)
     * @param width Chiều rộng
     * @param height Chiều cao
     */
    public Shield(double x, double y, double width, double height) {
        super(new Vector2D(x + width/2, y + height/2), width, height);
        this.health = MAX_HEALTH;
    }

    /**
     * Update shield (không cần logic đặc biệt).
     */
    @Override
    public void update() {
        // Shield không cần update logic
    }

    /**
     * Render shield lên canvas.
     * @param gc GraphicsContext để vẽ
     */
    @Override
    public void render(GraphicsContext gc) {
        draw(gc);
    }

    /**
     * Xử lý khi shield bị hit.
     * Giảm health và kích hoạt glow effect.
     */
    public void onHit() {
        health--;
        glowTimer = System.currentTimeMillis(); // kích hoạt hiệu ứng sáng
        if (health <= 0) {
            this.active = false; // Deactivate khi hết health
        }
    }

    /**
     * Vẽ shield với glow effect khi bị hit.
     * Màu sắc và độ sáng thay đổi theo health và thời gian glow.
     * 
     * @param gc GraphicsContext để vẽ
     */
    public void draw(GraphicsContext gc) {
        // Tính toán tọa độ góc trên trái từ center position
        double x = position.getX() - width / 2;
        double y = position.getY() - height / 2;
        
        double elapsed = System.currentTimeMillis() - glowTimer;
        boolean glowing = elapsed < GLOW_DURATION;

        // Màu sáng hơn khi va chạm
        Color fillColor = glowing
                ? Color.rgb(100, 255, 255, 0.9) // sáng mạnh
                : Color.rgb(0, 255, 255, 0.5); // bình thường

        Color borderColor = glowing
                ? Color.rgb(255, 255, 255, 1.0) // viền sáng trắng
                : Color.rgb(100, 255, 255, 0.8);

        gc.setFill(fillColor);
        gc.fillRoundRect(x, y, width, height, 10, 10);

        gc.setStroke(borderColor);
        gc.setLineWidth(glowing ? 3.5 : 2.0);
        gc.strokeRoundRect(x, y, width, height, 10, 10);

        // Vẽ các vết nứt mờ theo số lần bị trúng (health giảm)
        if (health == 2) {
            gc.setStroke(Color.rgb(255, 255, 255, 0.3));
            gc.strokeLine(x + width * 0.3, y + 3, x + width * 0.7, y + height - 3);
        } else if (health == 1) {
            gc.setStroke(Color.rgb(255, 200, 200, 0.4));
            gc.strokeLine(x + 5, y + 5, x + width - 5, y + height - 5);
            gc.strokeLine(x + width - 5, y + 5, x + 5, y + height - 5);
        }
    }

    /**
     * Kiểm tra collision với ball.
     * Sử dụng rectangle-rectangle intersection.
     * 
     * @param ball Ball cần kiểm tra
     * @return true nếu có collision, false nếu không
     */
    public boolean collidesWith(Ball ball) {
        double bx = ball.getPosition().getX();
        double by = ball.getPosition().getY();
        double br = ball.getRadius();
        
        // Tính toán bounds từ center position
        double x = position.getX() - width / 2;
        double y = position.getY() - height / 2;
        
        Rectangle2D rect = new Rectangle2D(x, y, width, height);
        Rectangle2D ballRect = new Rectangle2D(bx - br, by - br, br * 2, br * 2);
        return rect.intersects(ballRect);
    }

    /**
     * Xử lý khi shield bị hit (wrapper method).
     */
    public void hit() {
        onHit();
    }

    /**
     * Kiểm tra shield đã hỏng chưa.
     * 
     * @return true nếu shield hết health hoặc inactive, false nếu còn hoạt động
     */
    public boolean isBroken() {
        return health <= 0 || !active;
    }

    /**
     * Lấy tọa độ Y của góc trên trái.
     * 
     * @return Tọa độ Y góc trên trái
     */
    public double getY() {
        return position.getY() - height / 2; // Trả về Y của góc trên trái
    }

    // ==================== Getters ====================
    
    /**
     * Lấy health hiện tại của shield.
     * 
     * @return Health (0-3)
     */
    public int getHealth() {
        return health;
    }
}
