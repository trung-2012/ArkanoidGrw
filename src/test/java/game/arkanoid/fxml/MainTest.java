package game.arkanoid.fxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import game.arkanoid.models.Ball;
import game.arkanoid.models.Brick;
import game.arkanoid.models.NormalBrick;
import game.arkanoid.models.Paddle;
import game.arkanoid.models.PowerUp;
import game.arkanoid.models.PowerUpType;
import game.arkanoid.models.Shield;
import game.arkanoid.player_manager.Player;
import game.arkanoid.utils.Vector2D;

/**
 * Unit tests cho các chức năng & logic cơ bản của game Arkanoid.
 * Bao gồm: Player, Ball, Paddle, Brick, PowerUp, Shield, Vector2D
 */
public class MainTest {

    private Player player;
    private Ball ball;
    private Paddle paddle;
    private Brick brick;
    private Shield shield;
    private PowerUp powerUp;
    private Vector2D vector;

    @Before
    public void setUp() {
        // Khởi tạo các objects trước mỗi test
        player = new Player("testuser", "password123");
        ball = new Ball(new Vector2D(400, 300), 10);
        paddle = new Paddle(new Vector2D(400, 500));
        brick = new NormalBrick(new Vector2D(100, 100));
        shield = new Shield(0, 550, 800, 20);
        powerUp = new PowerUp(200, 200, PowerUpType.LASER);
        vector = new Vector2D(3.0, 4.0);
    }

    /**
     * Test 1: Kiểm tra khởi tạo Player
     */
    @Test
    public void testPlayerInitialization() {
        assertNotNull("Player không được null", player);
        assertEquals("Username phải khớp", "testuser", player.getUsername());
        assertEquals("Password phải khớp", "password123", player.getPassword());
        assertEquals("High score ban đầu phải là 0", 0, player.getHighScore());
        assertEquals("Nickname ban đầu phải là null", null, player.getNickname());
    }

    /**
     * Test 2: Kiểm tra cập nhật high score
     */
    @Test
    public void testPlayerHighScoreUpdate() {
        player.setHighScore(1000);
        assertEquals("High score sau update phải là 1000", 1000, player.getHighScore());
        
        player.setHighScore(2500);
        assertEquals("High score sau update lần 2 phải là 2500", 2500, player.getHighScore());
    }

    /**
     * Test 3: Kiểm tra set nickname
     */
    @Test
    public void testPlayerNickname() {
        player.setNickname("ProGamer");
        assertEquals("Nickname phải là ProGamer", "ProGamer", player.getNickname());
    }

    /**
     * Test 4: Kiểm tra Ball position và radius
     */
    @Test
    public void testBallProperties() {
        assertEquals("Ball X position phải là 400", 400.0, ball.getPosition().getX(), 0.001);
        assertEquals("Ball Y position phải là 300", 300.0, ball.getPosition().getY(), 0.001);
        assertEquals("Ball radius phải là 10", 10.0, ball.getRadius(), 0.001);
    }

    /**
     * Test 5: Kiểm tra Ball movement
     */
    @Test
    public void testBallMovement() {
        ball.setVelocity(new Vector2D(5.0, -5.0));
        ball.update();
        
        assertEquals("Ball X sau update phải là 405", 405.0, ball.getPosition().getX(), 0.001);
        assertEquals("Ball Y sau update phải là 295", 295.0, ball.getPosition().getY(), 0.001);
    }

    /**
     * Test 6: Kiểm tra Paddle movement
     */
    @Test
    public void testPaddleMovement() {
        double initialX = paddle.getPosition().getX();
        
        paddle.moveLeft();
        assertTrue("Paddle phải di chuyển sang trái", paddle.getPosition().getX() < initialX);
        
        double afterLeftX = paddle.getPosition().getX();
        paddle.moveRight();
        assertTrue("Paddle phải di chuyển sang phải", paddle.getPosition().getX() > afterLeftX);
    }

    /**
     * Test 7: Kiểm tra Brick health và destruction
     */
    @Test
    public void testBrickDestruction() {
        assertFalse("Brick ban đầu chưa bị phá hủy", brick.isDestroyed());
        assertEquals("NormalBrick có 1 health", 1, brick.getHealth());
        
        brick.setDestroyed(true);
        assertTrue("Brick sau setDestroyed phải bị phá hủy", brick.isDestroyed());
    }

    /**
     * Test 8: Kiểm tra Shield health
     */
    @Test
    public void testShieldHealth() {
        assertEquals("Shield ban đầu có 3 health", 3, shield.getHealth());
        assertFalse("Shield ban đầu chưa broken", shield.isBroken());
        
        shield.hit();
        assertEquals("Shield sau 1 hit có 2 health", 2, shield.getHealth());
        assertFalse("Shield với 2 health chưa broken", shield.isBroken());
        
        shield.hit();
        shield.hit();
        assertTrue("Shield sau 3 hits phải broken", shield.isBroken());
    }

    /**
     * Test 9: Kiểm tra PowerUp type
     */
    @Test
    public void testPowerUpType() {
        assertEquals("PowerUp type phải là LASER", PowerUpType.LASER, powerUp.getType());
        
        PowerUp extraLifePowerUp = new PowerUp(300, 300, PowerUpType.EXTRA_LIFE);
        assertEquals("PowerUp type phải là EXTRA_LIFE", PowerUpType.EXTRA_LIFE, extraLifePowerUp.getType());
    }

    /**
     * Test 10: Kiểm tra Vector2D magnitude (độ dài vector)
     */
    @Test
    public void testVectorMagnitude() {
        // Vector (3, 4) có magnitude = 5 (theo định lý Pythagoras)
        double magnitude = vector.magnitude();
        assertEquals("Vector (3,4) phải có magnitude = 5", 5.0, magnitude, 0.001);
        
        Vector2D unitVector = new Vector2D(3.0, 4.0);
        unitVector.normalize();
        assertEquals("Vector đã normalize phải có magnitude = 1", 1.0, unitVector.magnitude(), 0.001);
    }
}
