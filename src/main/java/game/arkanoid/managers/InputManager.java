package game.arkanoid.managers;

import game.arkanoid.models.Ball;
import game.arkanoid.models.Paddle;
import game.arkanoid.utils.GameConstants;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.Canvas;

public class InputManager {
    // Trạng thái phím bấm
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    // References to game objects
    private Paddle paddle;
    private Canvas canvas;
    
    // Callback interface để thông báo khi phóng bóng
    public interface SpaceCallback {
        void onSpacePressed();
    }
    
    private SpaceCallback spaceCallback;
    
    public InputManager(Paddle paddle, Canvas canvas) {
        this.paddle = paddle;
        this.canvas = canvas;
    }
    
    // Cập nhật vị trí paddle dựa trên trạng thái phím bấm
    public void updatePaddleMovement() {
        if (paddle == null) return;
        
        // Di chuyển paddle theo input
        if (leftPressed && !rightPressed) {
            paddle.moveLeft();
        }
        if (rightPressed && !leftPressed) {
            paddle.moveRight();
        }
        
        // Đảm bảo paddle không đi ra ngoài màn hình
        double half = paddle.getWidth() / 2.0;
        double canvasW = (canvas != null) ? canvas.getWidth() : GameConstants.WINDOW_WIDTH;
        
        if (paddle.getPosition().getX() - half < 0) {
            paddle.getPosition().setX(half);
        }
        if (paddle.getPosition().getX() + half > canvasW) {
            paddle.getPosition().setX(canvasW - half);
        }
    }
    
    // Xử lý phím Space được bấm để phóng bóng
    public void handleSpacePressed(Ball ball, boolean ballAttachedToPaddle) {
        if (ballAttachedToPaddle && ball != null) {
            // Phóng bóng theo hướng ngẫu nhiên
            double diag = GameConstants.BALL_SPEED / Math.sqrt(2.0);
            double dir = Math.random() < 0.5 ? -1 : 1;
            ball.setVelocity(new Vector2D(diag * dir, -diag));
            
            // Notify callback để GameEngine cập nhật trạng thái
            if (spaceCallback != null) {
                spaceCallback.onSpacePressed();
            }
        }
    }
    
    // Getters and Setters
    public void setLeftPressed(boolean pressed) {
        this.leftPressed = pressed;
    }
    
    public void setRightPressed(boolean pressed) {
        this.rightPressed = pressed;
    }
    
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
    
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
    
    public void setSpaceCallback(SpaceCallback callback) {
        this.spaceCallback = callback;
    }
}
