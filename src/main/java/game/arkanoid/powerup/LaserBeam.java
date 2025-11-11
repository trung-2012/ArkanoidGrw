package game.arkanoid.powerup;

import game.arkanoid.models.Brick;
import game.arkanoid.models.GameObject;
import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

/**
 * Lớp LaserBeam đại diện cho tia laser bắn ra từ paddle khi có LASER power-up.
 * Kế thừa từ GameObject và di chuyển theo hướng lên trên để phá gạch.
 * 
 * <p>Laser có trail effect (vệt đuôi) để tạo hiệu ứng chuyển động mượt mà.
 * Paddle có thể bắn laser liên tục bằng cách giữ phím Space.</p>
 * 
 * <p>Đặc điểm:</p>
 * <ul>
 *   <li>Kích thước: 15x30 pixels</li>
 *   <li>Tốc độ: 12 pixels/frame (di chuyển lên trên)</li>
 *   <li>Trail: Lưu 15 vị trí gần nhất để vẽ hiệu ứng đuôi</li>
 *   <li>Tự động xóa khi ra ngoài màn hình</li>
 * </ul>
 * 
 * @author ArkanoidGrw
 * @version 1.0
 * @see PowerUpType#LASER
 * @see GameObject
 */
public class LaserBeam extends GameObject {
    /** Chiều rộng mặc định của laser beam (15 pixels) */
    private static final double DEFAULT_WIDTH = 15;
    
    /** Chiều cao mặc định của laser beam (30 pixels) */
    private static final double DEFAULT_HEIGHT = 30;
    
    /** Tốc độ di chuyển mặc định (12 pixels/frame, hướng lên trên) */
    private static final double DEFAULT_SPEED = 12;
    
    /** Số lượng điểm tối đa trong trail để vẽ hiệu ứng đuôi */
    private static final int TRAIL_MAX = 15;

    /** Tốc độ di chuyển hiện tại */
    private double speed;
    
    /** Hình ảnh bullet (có thể null) */
    private Image bulletImage;
    
    /** Danh sách các vị trí trước đó để vẽ hiệu ứng vệt đuôi (trail) */
    private final java.util.List<Vector2D> trail = new java.util.LinkedList<>();

    /**
     * Constructor khởi tạo LaserBeam tại vị trí cho trước.
     * 
     * @param position Vị trí khởi tạo (center của laser)
     * @param bulletImage Hình ảnh bullet (có thể null, sẽ dùng màu mặc định)
     */
    public LaserBeam(Vector2D position, Image bulletImage) {
        super(position, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.speed = DEFAULT_SPEED;
        this.bulletImage = bulletImage;
    }

    /**
     * Cập nhật vị trí laser (di chuyển lên trên) và trail effect.
     * Lưu vị trí hiện tại vào trail và giới hạn số lượng điểm trail.
     * Được gọi mỗi frame bởi PowerUpManager.
     */
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

    /**
     * Render laser beam lên canvas.
     * Sử dụng image nếu có, nếu không dùng màu cyan mặc định.
     * Trail effect được render bởi RenderManager trước khi gọi method này.
     * 
     * @param gc GraphicsContext để vẽ
     */
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

    /**
     * Lấy danh sách trail positions để vẽ hiệu ứng đuôi.
     * 
     * @return List các Vector2D chứa vị trí của trail (tối đa 15 điểm)
     */
    public java.util.List<Vector2D> getTrail() {
        return trail;
    }

    /**
     * Kiểm tra laser đã ra ngoài màn hình chưa.
     * Laser được xóa khi hoàn toàn ra khỏi màn hình phía trên.
     * 
     * @param canvasHeight Chiều cao của canvas
     * @return true nếu laser đã ra ngoài màn hình, false nếu còn trong màn hình
     */
    public boolean isOffScreen(double canvasHeight) {
        return position.getY() + height < 0;
    }

    /**
     * Kiểm tra va chạm với gạch bằng rectangle intersection.
     * Sử dụng bounds của laser và brick để detect collision.
     * 
     * @param brick Gạch cần kiểm tra va chạm
     * @return true nếu laser chạm gạch, false nếu không
     */
    public boolean intersects(Brick brick) {
        double bx = brick.getPosition().getX();
        double by = brick.getPosition().getY();

        return position.getX() + width / 2 >= bx &&
                position.getX() - width / 2 <= bx + GameConstants.BRICK_WIDTH &&
                position.getY() >= by &&
                position.getY() - height <= by + GameConstants.BRICK_HEIGHT;
    }
}
