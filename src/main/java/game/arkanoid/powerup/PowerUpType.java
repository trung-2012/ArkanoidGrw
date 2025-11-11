package game.arkanoid.powerup;

/**
 * Enum định nghĩa các loại power-up trong game Arkanoid.
 * Mỗi power-up có hiệu ứng khác nhau khi được thu thập.
 * 
 * <p>Các loại power-up:</p>
 * <ul>
 *   <li>EXTRA_LIFE - Thêm 1 mạng cho người chơi</li>
 *   <li>LASER - Cho phép paddle bắn laser phá gạch</li>
 *   <li>SHIELD - Tạo tấm khiên bảo vệ paddle (3 lần chịu đòn)</li>
 *   <li>MULTI_BALL - Nhân đôi số lượng bóng hiện tại</li>
 *   <li>WEAK - Giảm tốc độ bóng (MIN_BALL_SPEED)</li>
 *   <li>STRONG - Tăng tốc độ bóng (MAX_BALL_SPEED)</li>
 *   <li>PADDLE_GROW - Tăng kích thước paddle lên 150px</li>
 *   <li>PADDLE_SHRINK - Giảm kích thước paddle xuống 70px</li>
 * </ul>
 * 
 * @author ArkanoidGrw
 * @version 1.0
 * @see PowerUp
 * @see Shield
 */
public enum PowerUpType {
    /** Thêm 1 mạng cho người chơi */
    EXTRA_LIFE,
    
    /** Cho phép paddle bắn laser để phá gạch */
    LASER,
    
    /** Tạo tấm khiên bảo vệ paddle với 3 health */
    SHIELD,
    
    /** Nhân đôi số lượng bóng hiện tại (tối đa MAX_BALLS) */
    MULTI_BALL,
    
    /** Giảm tốc độ bóng xuống MIN_BALL_SPEED (3.0), hết hạn sau 15s */
    WEAK,
    
    /** Tăng tốc độ bóng lên MAX_BALL_SPEED (5.0), hết hạn sau 15s */
    STRONG,
    
    /** Tăng kích thước paddle lên 150px, hết hạn sau 20s */
    PADDLE_GROW,
    
    /** Giảm kích thước paddle xuống 70px, hết hạn sau 20s */
    PADDLE_SHRINK
}
