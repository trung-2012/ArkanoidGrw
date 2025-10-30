package game.arkanoid.models;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shield {
    private double x, y, width, height;
    private int health = 3;

    // Thời gian sáng (ms)
    private long glowTimer = 0;
    private static final long GLOW_DURATION = 150; // sáng trong 150ms

    public Shield(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Gọi khi bóng va chạm với shield */
    public void onHit() {
        health--;
        glowTimer = System.currentTimeMillis(); // kích hoạt hiệu ứng sáng
    }

    /** Vẽ tấm khiên, tự thay đổi độ sáng nếu đang được kích hoạt */
    public void draw(GraphicsContext gc) {
        double elapsed = System.currentTimeMillis() - glowTimer;
        boolean glowing = elapsed < GLOW_DURATION;

        // Màu sáng hơn khi va chạm
        Color fillColor = glowing
                ? Color.rgb(100, 255, 255, 0.9) // sáng mạnh
                : Color.rgb(0, 255, 255, 0.5);  // bình thường

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
        Rectangle2D rect = new Rectangle2D(x, y, width, height);
        Rectangle2D ballRect = new Rectangle2D(bx - br, by - br, br * 2, br * 2);
        return rect.intersects(ballRect);
    }

    public void hit() {
        onHit();
    }

    public boolean isBroken() {
        return health <= 0;
    }

    public double getY() { return y; }
}
