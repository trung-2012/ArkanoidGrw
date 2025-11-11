package game.arkanoid.powerup;

import game.arkanoid.models.GameObject;
import game.arkanoid.models.Paddle;
import game.arkanoid.utils.Vector2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Lớp PowerUp đại diện cho các vật phẩm power-up rơi xuống từ gạch bị phá.
 * Kế thừa từ GameObject và tự động rơi xuống theo trọng lực.
 * 
 * <p>Power-up xuất hiện với xác suất 20% khi phá gạch (trừ khi đã hết gạch).
 * Người chơi cần điều khiển paddle để thu thập power-up trước khi nó rơi ra ngoài màn hình.</p>
 * 
 * <p>Kích thước: 24x24 pixels, tốc độ rơi: 1.0 pixels/frame</p>
 * 
 * @author ArkanoidGrw
 * @version 1.0
 * @see PowerUpType
 * @see GameObject
 */
public class PowerUp extends GameObject {
    /** Kích thước mặc định của power-up (24x24 pixels) */
    private static final double DEFAULT_SIZE = 24;
    
    /** Tốc độ rơi mặc định (1.0 pixels/frame) */
    private static final double DEFAULT_SPEED = 1.0;
    
    /** Tốc độ rơi hiện tại */
    private double speed;
    
    /** Loại power-up */
    private PowerUpType type;
    
    /** Hình ảnh của power-up (load từ resources) */
    private Image image;

    /**
     * Constructor khởi tạo PowerUp tại vị trí cho trước.
     * Tự động load hình ảnh tương ứng với loại power-up từ resources.
     * 
     * @param x Tọa độ X của center
     * @param y Tọa độ Y của center
     * @param type Loại power-up (EXTRA_LIFE, LASER, SHIELD, etc.)
     */
    public PowerUp(double x, double y, PowerUpType type) {
        super(new Vector2D(x, y), DEFAULT_SIZE, DEFAULT_SIZE);
        this.speed = DEFAULT_SPEED;
        this.type = type;
        try {
            String path = "/game/arkanoid/images/" + type.name().toLowerCase() + ".png";
            this.image = new Image(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            this.image = null; // fallback nếu không có ảnh
        }
    }

    /**
     * Cập nhật vị trí power-up (rơi xuống theo trọng lực).
     * Được gọi mỗi frame bởi PowerUpManager.
     */
    @Override
    public void update() {
        position.setY(position.getY() + speed);
    }

    /**
     * Render power-up lên canvas.
     * Logic render được xử lý bởi RenderManager.
     * 
     * @param gc GraphicsContext để vẽ (không sử dụng)
     */
    @Override
    public void render(GraphicsContext gc) {
        // Render logic sẽ được xử lý bởi RenderManager
    }

    /**
     * Kiểm tra va chạm với paddle bằng rectangle intersection.
     * Sử dụng bounds của cả power-up và paddle để detect collision.
     * 
     * @param paddle Paddle cần kiểm tra va chạm
     * @return true nếu power-up chạm paddle, false nếu không
     */
    public boolean intersects(Paddle paddle) {
        Rectangle2D rect1 = new Rectangle2D(
            position.getX() - width / 2, 
            position.getY() - height / 2, 
            width, 
            height
        );
        Rectangle2D rect2 = new Rectangle2D(
            paddle.getPosition().getX() - paddle.getWidth() / 2,
            paddle.getPosition().getY() - paddle.getHeight() / 2,
            paddle.getWidth(), 
            paddle.getHeight()
        );
        return rect1.intersects(rect2);
    }

    // Getters
    
    /**
     * Lấy tọa độ X của center.
     * 
     * @return Tọa độ X
     */
    public double getX() {
        return position.getX();
    }

    /**
     * Lấy tọa độ Y của center.
     * 
     * @return Tọa độ Y
     */
    public double getY() {
        return position.getY();
    }

    /**
     * Lấy kích thước power-up (width = height = size).
     * 
     * @return Kích thước (24 pixels)
     */
    public double getSize() {
        return width; // size = width = height
    }

    /**
     * Lấy hình ảnh của power-up.
     * 
     * @return Image object, hoặc null nếu không load được
     */
    public Image getImage() {
        return image;
    }

    /**
     * Lấy loại power-up.
     * 
     * @return PowerUpType (EXTRA_LIFE, LASER, SHIELD, etc.)
     */
    public PowerUpType getType() {
        return type;
    }
}
