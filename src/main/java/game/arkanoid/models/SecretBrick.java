package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SecretBrick - Gạch bí mật với khả năng biến hình
 * 
 * Đặc điểm: Cứ mỗi 10 giây, gạch này sẽ biến thành một loại gạch khác ngẫu nhiên
 * (trừ chính nó). Tất cả các loại gạch khác đều có tỉ lệ như nhau.
 * 
 * Cơ chế hoạt động: SecretBrick THỰC SỰ biến thành gạch khác
 */
public class SecretBrick extends Brick {
    /** Ảnh của SecretBrick (hiện không dùng vì luôn hiển thị ảnh của disguise) */
    private static Image brickImage;
    
    /** Thời gian transform (10 giây = 10000ms) */
    private static final long TRANSFORM_INTERVAL = 10000;
    
    /** Thời điểm transform gần nhất */
    private long lastTransformTime;
    
    /** Gạch ngụy trang hiện tại */
    private Brick disguiseBrick;
    
    /** Danh sách các BrickType có thể biến thành (tất cả trừ SECRET) */
    private static final BrickType[] AVAILABLE_TYPES = {
        BrickType.NORMAL,
        BrickType.WOOD,
        BrickType.IRON,
        BrickType.GOLD,
        BrickType.EXPLODE,
        BrickType.INSANE
    };
    
    static {
        try {
            brickImage = new Image(SecretBrick.class.getResourceAsStream("/game/arkanoid/images/BrickSecret.png"));
        } catch (Exception e) {
            System.err.println("Cannot load SecretBrick image: " + e.getMessage());
        }
    }
    
    /**
     * Constructor khởi tạo SecretBrick.
     * Không tạo disguise brick ngay - chờ 10s đầu để hiển thị ảnh secret.
     * 
     * @param position Vị trí góc trên trái của gạch
     */
    public SecretBrick(Vector2D position) {
        super(position, 1000, 1); // Khởi tạo với giá trị tạm
        this.lastTransformTime = System.currentTimeMillis();
        this.disguiseBrick = null; // Chưa có disguise, sẽ tạo sau 10s
    }
    
    /**
     * Update logic: kiểm tra xem đã đến lúc transform chưa.
     * Lần đầu tiên (sau 10s) sẽ tạo disguise brick.
     * Những lần sau sẽ transform sang gạch khác.
     * Luôn đồng bộ trạng thái từ disguise brick nếu có.
     */
    @Override
    public void update() {
        if (!active) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTransformTime >= TRANSFORM_INTERVAL) {
            // Transform sang gạch khác (hoặc tạo lần đầu nếu chưa có)
            transform();
            lastTransformTime = currentTime;
        }
        
        // Update disguise brick nếu đã có
        if (disguiseBrick != null) {
            disguiseBrick.update();
            // Đồng bộ trạng thái từ disguise brick
            syncFromDisguise();
        }
    }
    
    /**
     * Render gạch: 
     * - Nếu chưa có disguise (10s đầu): hiển thị ảnh BrickSecret.png
     * - Nếu đã có disguise: hiển thị hình ảnh của disguise brick
     * 
     * @param gc GraphicsContext để vẽ
     */
    @Override
    public void render(GraphicsContext gc) {
        if (!active) return;
        
        if (disguiseBrick != null) {
            // Đã transform - hiển thị disguise brick
            disguiseBrick.render(gc);
        } else if (brickImage != null) {
            // Chưa transform - hiển thị ảnh SecretBrick
            gc.drawImage(brickImage, position.getX(), position.getY(), width, height);
        }
    }
    
    /**
     * Lấy ảnh của gạch hiện tại (trả về ảnh của disguise).
     * 
     * @return Image của disguise brick
     */
    @Override
    public Image getBrickImage() {
        if (disguiseBrick != null) {
            return disguiseBrick.getBrickImage();
        }
        return brickImage;
    }
    
    /**
     * Xử lý khi bị damage:
     * - Nếu chưa có disguise (10s đầu): giảm health trực tiếp của SecretBrick
     * - Nếu đã có disguise: delegate sang disguise brick và sync trạng thái
     */
    @Override
    public void takeDamage() {
        if (!active) return;
        
        if (disguiseBrick != null) {
            // Đã có disguise - delegate sang disguise brick
            disguiseBrick.takeDamage();
            // Đồng bộ trạng thái từ disguise brick
            syncFromDisguise();
        } else {
            // Chưa có disguise - xử lý như brick thường (health = 1)
            this.health--;
            onDamage();
            
            if (this.health <= 0) {
                this.active = false;
                onDestroyed();
            }
        }
    }
    
    /**
     * Hook method khi brick bị destroyed.
     * Được gọi khi disguise brick bị phá hủy.
     */
    @Override
    protected void onDestroyed() {
        super.onDestroyed();
        // Có thể thêm logic đặc biệt khi SecretBrick bị phá hủy
    }
    
    /**
     * Transform sang một loại gạch ngẫu nhiên khác.
     * Tạo disguise brick mới với cùng vị trí và đồng bộ thuộc tính.
     */
    private void transform() {
        disguiseBrick = createRandomDisguise();
        syncFromDisguise();
    }
    
    /**
     * Đồng bộ thuộc tính từ disguise brick sang SecretBrick.
     * Bao gồm: health, points, active status.
     * Điều này đảm bảo SecretBrick có CHÍNH XÁC tính chất của gạch đang ngụy trang.
     */
    private void syncFromDisguise() {
        if (disguiseBrick != null) {
            this.health = disguiseBrick.getHealth();
            this.points = disguiseBrick.getPoints();
            this.active = !disguiseBrick.isDestroyed();
        }
    }
    
    /**
     * Tạo một disguise brick ngẫu nhiên.
     * Chọn ngẫu nhiên từ AVAILABLE_TYPES và tạo brick tương ứng.
     * 
     * @return Brick mới được tạo
     */
    private Brick createRandomDisguise() {
        int randomIndex = ThreadLocalRandom.current().nextInt(AVAILABLE_TYPES.length);
        BrickType selectedType = AVAILABLE_TYPES[randomIndex];
        
        // Tạo brick dựa trên type
        switch (selectedType) {
            case NORMAL:
                return new NormalBrick(new Vector2D(position.getX(), position.getY()));
            case WOOD:
                return new WoodBrick(new Vector2D(position.getX(), position.getY()));
            case IRON:
                return new IronBrick(new Vector2D(position.getX(), position.getY()));
            case GOLD:
                return new GoldBrick(new Vector2D(position.getX(), position.getY()));
            case EXPLODE:
                return new ExplodeBrick(new Vector2D(position.getX(), position.getY()));
            case INSANE:
                return new InsaneBrick(new Vector2D(position.getX(), position.getY()));
            default:
                return new NormalBrick(new Vector2D(position.getX(), position.getY()));
        }
    }
    
    /**
     * Lấy disguise brick hiện tại (cho debugging/testing).
     * 
     * @return Disguise brick đang được sử dụng
     */
    public Brick getDisguiseBrick() {
        return disguiseBrick;
    }
    
    /**
     * Lấy thời gian còn lại đến lần transform tiếp theo (milliseconds).
     * 
     * @return Thời gian còn lại (ms)
     */
    public long getTimeUntilNextTransform() {
        long elapsed = System.currentTimeMillis() - lastTransformTime;
        return Math.max(0, TRANSFORM_INTERVAL - elapsed);
    }
}
