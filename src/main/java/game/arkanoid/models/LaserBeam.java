package game.arkanoid.models;

import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;


public class LaserBeam extends GameObject {
    private static final double DEFAULT_WIDTH = 15;
    private static final double DEFAULT_HEIGHT = 30;
    private static final double DEFAULT_SPEED = 12;
    // Số lượng điểm tối đa trong trail của laser
    private static final int TRAIL_MAX = 15;

    private double speed;
    private Image bulletImage;
    // Danh sách các vị trí trước đó để vẽ hiệu ứng vệt đuôi (trail)
    private final java.util.List<Vector2D> trail = new java.util.LinkedList<>();

    public LaserBeam(Vector2D position, Image bulletImage) {
        super(position, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.speed = DEFAULT_SPEED;
        this.bulletImage = bulletImage;
    }

    @Override
    public void update() {
        // Cập nhật vị trí đi lên
        position.setY(position.getY() - speed);
        // Lưu vị trí hiện tại vào trail (copy để không bị thay đổi qua tham chiếu)
        trail.add(new Vector2D(position.getX(), position.getY()));
        if (trail.size() > TRAIL_MAX) {
            trail.remove(0); // bỏ điểm cũ nhất
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Việc vẽ trail được handle bởi RenderManager trước khi vẽ thân laser
        if (bulletImage != null) {
            gc.drawImage(bulletImage,
                    position.getX() - width / 2,
                    position.getY() - height / 2,
                    width,
                    height);
        } else {
            gc.setFill(Color.rgb(0, 215, 240));
            gc.fillRect(position.getX() - width / 2, position.getY() - height / 2, width, height);
        }
    }

    public java.util.List<Vector2D> getTrail() {
        return trail;
    }

    public boolean isOffScreen(double canvasHeight) {
        return position.getY() + height < 0;
    }

    public boolean intersects(Brick brick) {
        double bx = brick.getPosition().getX();
        double by = brick.getPosition().getY();

        return position.getX() + width / 2 >= bx &&
                position.getX() - width / 2 <= bx + GameConstants.BRICK_WIDTH &&
                position.getY() >= by &&
                position.getY() - height <= by + GameConstants.BRICK_HEIGHT;
    }
}
