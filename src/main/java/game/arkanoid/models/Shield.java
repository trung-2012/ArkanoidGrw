package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shield extends GameObject {
    private static final int MAX_HEALTH = 3;
    private static final long GLOW_DURATION = 150; // sáng trong 150ms
    
    private int health;
    // Thời gian sáng (ms)
    private long glowTimer = 0;

    public Shield(double x, double y, double width, double height) {
        super(new Vector2D(x + width/2, y + height/2), width, height);
        this.health = MAX_HEALTH;
    }

    @Override
    public void update() {
        // Shield không cần update logic
    }

    @Override
    public void render(GraphicsContext gc) {
        draw(gc);
    }

    // Xử lý khi shield bị trúng
    public void onHit() {
        health--;
        glowTimer = System.currentTimeMillis(); // kích hoạt hiệu ứng sáng
        if (health <= 0) {
            this.active = false; // Deactivate khi hết health
        }
    }

    // Vẽ tấm khiên, tự thay đổi độ sáng nếu đang được kích hoạt
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

    public void hit() {
        onHit();
    }

    public boolean isBroken() {
        return health <= 0 || !active;
    }

    public double getY() {
        return position.getY() - height / 2; // Trả về Y của góc trên trái
    }

    // Getters
    
    public int getHealth() {
        return health;
    }
}
