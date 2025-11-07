package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp abstract GameObject đại diện cho tất cả các đối tượng trong game.
 * Đây là base class cho Ball, Paddle, Brick, PowerUp, v.v.
 * Áp dụng Abstraction trong OOP.
 * 
 * @author ArkanoidGrw
 * @version 1.0
 */
public abstract class GameObject {
    /** Vị trí trung tâm của object */
    protected Vector2D position;
    
    /** Chiều rộng của object */
    protected double width;
    
    /** Chiều cao của object */
    protected double height;
    
    /** Trạng thái hoạt động của object (true = đang hoạt động, false = bị deactivate) */
    protected boolean active;

    /**
     * Constructor khởi tạo GameObject với vị trí và kích thước.
     * Mặc định object được tạo ở trạng thái active.
     * 
     * @param position Vị trí trung tâm của object (Vector2D)
     * @param width Chiều rộng của object (pixels)
     * @param height Chiều cao của object (pixels)
     */
    public GameObject(Vector2D position, double width, double height) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.active = true;
    }

    /**
     * Cập nhật trạng thái của object mỗi frame.
     * Abstract method - bắt buộc phải implement trong subclass.
     * Được gọi bởi game loop (60 FPS).
     */
    public abstract void update();

    /**
     * Vẽ object lên canvas.
     * Abstract method - bắt buộc phải implement trong subclass.
     * 
     * @param gc GraphicsContext để vẽ object
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Kiểm tra va chạm với object khác sử dụng AABB (Axis-Aligned Bounding Box).
     * Tính toán dựa trên vị trí trung tâm và kích thước của cả hai object.
     * 
     * @param other Object khác cần kiểm tra va chạm
     * @return true nếu có va chạm, false nếu không
     */
    public boolean collidesWith(GameObject other) {
        if (other == null || !other.isActive() || !this.isActive()) {
            return false;
        }
        
        double thisLeft = position.getX() - width / 2;
        double thisRight = position.getX() + width / 2;
        double thisTop = position.getY() - height / 2;
        double thisBottom = position.getY() + height / 2;
        
        double otherLeft = other.position.getX() - other.width / 2;
        double otherRight = other.position.getX() + other.width / 2;
        double otherTop = other.position.getY() - other.height / 2;
        double otherBottom = other.position.getY() + other.height / 2;
        
        return thisLeft < otherRight && thisRight > otherLeft &&
               thisTop < otherBottom && thisBottom > otherTop;
    }

    /**
     * Xử lý khi va chạm với object khác.
     * Default implementation: không làm gì.
     * Subclass có thể override để thêm logic riêng.
     * 
     * @param other Object va chạm
     */
    public void onCollision(GameObject other) {
        // Default: không làm gì
        // Subclass override để thêm logic riêng
    }

    /**
     * Kiểm tra object có nằm ngoài bounds của game không.
     * Hữu ích để remove các object ra khỏi màn hình.
     * 
     * @param maxWidth Chiều rộng tối đa của game area
     * @param maxHeight Chiều cao tối đa của game area
     * @return true nếu object nằm ngoài bounds, false nếu còn trong màn hình
     */
    public boolean isOutOfBounds(double maxWidth, double maxHeight) {
        return position.getX() < 0 || position.getX() > maxWidth ||
               position.getY() < 0 || position.getY() > maxHeight;
    }

    // Getters & Setters
    
    /**
     * Lấy vị trí trung tâm của object.
     * 
     * @return Vector2D chứa tọa độ (x, y) của object
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * Thiết lập vị trí mới cho object.
     * 
     * @param position Vector2D chứa tọa độ (x, y) mới
     */
    public void setPosition(Vector2D position) {
        this.position = position;
    }

    /**
     * Lấy chiều rộng của object.
     * 
     * @return Chiều rộng (pixels)
     */
    public double getWidth() {
        return width;
    }

    /**
     * Thiết lập chiều rộng mới cho object.
     * 
     * @param width Chiều rộng mới (pixels)
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Lấy chiều cao của object.
     * 
     * @return Chiều cao (pixels)
     */
    public double getHeight() {
        return height;
    }

    /**
     * Thiết lập chiều cao mới cho object.
     * 
     * @param height Chiều cao mới (pixels)
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Kiểm tra object có đang hoạt động không.
     * 
     * @return true nếu object đang active, false nếu đã bị deactivate
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Thiết lập trạng thái hoạt động cho object.
     * 
     * @param active true để activate, false để deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Hủy kích hoạt object.
     * Sau khi gọi method này, object sẽ không còn hoạt động trong game.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Lấy vị trí trung tâm của object (tạo Vector2D mới).
     * 
     * @return Vector2D mới chứa tọa độ trung tâm
     */
    public Vector2D getCenter() {
        return new Vector2D(position.getX(), position.getY());
    }

    /**
     * Lấy bounding box của object.
     * Trả về mảng chứa [left, top, right, bottom].
     * 
     * @return Mảng double[] với 4 giá trị: [left, top, right, bottom]
     */
    public double[] getBounds() {
        return new double[] {
            position.getX() - width / 2,  // left
            position.getY() - height / 2, // top
            position.getX() + width / 2,  // right
            position.getY() + height / 2  // bottom
        };
    }
}
