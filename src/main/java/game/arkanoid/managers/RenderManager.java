package game.arkanoid.managers;

import game.arkanoid.models.*;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * RenderManager quản lý tất cả rendering logic cho game Arkanoid.
 * Tách biệt phần vẽ đồ họa khỏi game logic để dễ maintain và test.
 * 
 * @author ArkanoidGrw
 * @version 1.0
 */
public class RenderManager {

    private Canvas canvas;
    private GraphicsContext gc;

    // Images
    private Image ballImage;
    private Image paddleImage;

    // Effect state
    private double chargePulse = 0.0;
    
    // Screen shake offset
    private double shakeOffsetX = 0;
    private double shakeOffsetY = 0;

    /**
     * Constructor khởi tạo RenderManager với canvas.
     * 
     * @param canvas Canvas để vẽ game
     */
    public RenderManager(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    /**
     * Render tất cả game objects lên canvas.
     * Thứ tự render: bricks → paddle → trails → aura → balls → powerups → lasers → shield → effects.
     * 
     * @param mainBall Bóng chính (để render charge aura)
     * @param paddle Thanh paddle
     * @param bricks Danh sách gạch
     * @param powerUps Danh sách power-ups
     * @param laserBeams Danh sách laser beams
     * @param shield Shield (có thể null)
     * @param explosions Danh sách hiệu ứng nổ
     * @param debrisEffects Danh sách hiệu ứng mảnh vỡ
     * @param balls Danh sách tất cả các bóng
     * @param ballAttachedToPaddle true nếu bóng đang dính paddle
     */
    public void renderAll(
            Ball mainBall,
            Paddle paddle,
            List<Brick> bricks,
            List<PowerUp> powerUps,
            List<LaserBeam> laserBeams,
            Shield shield,
            List<ExplosionEffect> explosions,
            List<DebrisEffect> debrisEffects,
            List<Ball> balls,
            boolean ballAttachedToPaddle
    ) {
        if (gc == null) return;

        // Apply screen shake effect
        gc.save();
        gc.translate(shakeOffsetX, shakeOffsetY);

        // Clear canvas
        gc.clearRect(-shakeOffsetX, -shakeOffsetY, canvas.getWidth(), canvas.getHeight());
        renderBricks(bricks);
        renderPaddle(paddle);
        renderTrailsForAll(balls);
        renderLaserTrails(laserBeams);
        renderChargeAura(mainBall, ballAttachedToPaddle);
        for (Ball ball : balls) {
            renderBall(ball);
        }
        renderPowerUps(powerUps);
        renderLaserBeams(laserBeams);
        renderShield(shield);
        renderExplosions(explosions);
        renderDebrisEffects(debrisEffects);
        
        gc.restore();
    }

    // Render bricks
    private void renderBricks(List<Brick> bricks) {
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                brick.render(gc); // Polymorphic call
            }
        }
    }

    // Render paddle
    private void renderPaddle(Paddle paddle) {
        if (paddle == null) return;

        double pw = paddle.getWidth();
        double ph = paddle.getHeight();
        double px = paddle.getPosition().getX() - pw / 2.0;
        double py = paddle.getPosition().getY() - ph / 2.0;

        if (paddleImage != null) {
            gc.drawImage(paddleImage, px, py, pw, ph);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(px, py, pw, ph);
        }
    }

    // Render ball trail effect
    private void renderTrailsForAll(List<Ball> balls) {
        for (Ball ball : balls) {
            List<Vector2D> trail = ball.getTrail();

            for (int i = 0; i < trail.size(); i++) {
                Vector2D p = trail.get(i);

                double progress = (double) i / trail.size();
                double alpha = progress * 0.7;

                gc.setGlobalAlpha(alpha);
                gc.setFill(Color.web("#a0cfff", alpha));

                double size = ball.getRadius() * 2;

                gc.fillOval(p.getX() - size / 2, p.getY() - size / 2, size, size);
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    // Render lập lòe ball khi ở trên paddle trc lúc bantumlum
    private void renderChargeAura(Ball ball, boolean attached) {
        if (ball == null || !attached) return;

        double bx = ball.getPosition().getX();
        double by = ball.getPosition().getY();
        double r = ball.getRadius();

        double auraSize = r * (2.2 + chargePulse * 1.5);
        double alpha = 0.35 + chargePulse * 0.5;

        gc.setGlobalAlpha(alpha);
        gc.setFill(Color.web("#a6f6ff", alpha)); // neon cyan
        gc.fillOval(
                bx - auraSize,
                by - auraSize,
                auraSize * 2,
                auraSize * 2
        );
        gc.setGlobalAlpha(1.0);
    }

    // Render ball
    private void renderBall(Ball ball) {
        double bx = ball.getPosition().getX() - ball.getRadius();
        double by = ball.getPosition().getY() - ball.getRadius();
        double size = ball.getRadius() * 2;

        if (ballImage != null) {
            gc.drawImage(ballImage, bx, by, size, size);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillOval(bx, by, size, size);
        }
    }

    // Render power-ups
    private void renderPowerUps(List<PowerUp> powerUps) {
        for (PowerUp p : powerUps) {
            Image img = p.getImage();
            if (img != null) {
                gc.drawImage(img, p.getX() - p.getSize() / 2, p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
            } else {
                gc.setFill(Color.LIMEGREEN);
                gc.fillOval(p.getX() - p.getSize() / 2, p.getY() - p.getSize() / 2, p.getSize(), p.getSize());
            }
        }
    }

    // Render laser beams
    private void renderLaserBeams(List<LaserBeam> laserBeams) {
        for (LaserBeam beam : laserBeams) {
            beam.render(gc);
        }
    }

    // Render trail cho các laser beam (fading vertical streaks với màu theo paddle)
    private void renderLaserTrails(List<LaserBeam> laserBeams) {
        if (laserBeams == null) return;
        
        // Lấy màu trail từ GameSettings dựa trên paddle hiện tại
        String trailColor = game.arkanoid.utils.GameSettings.getInstance().getLaserTrailColor();
        
        for (LaserBeam beam : laserBeams) {
            java.util.List<Vector2D> trail = beam.getTrail();
            int size = trail.size();
            for (int i = 0; i < size; i++) {
                Vector2D p = trail.get(i);
                double progress = (double) i / size; // 0 (cũ) -> 1 (mới nhất)
                double alpha = progress * 0.6; // tăng độ sáng về phía đầu gần viên đạn
                double w = beam.getWidth() * (0.5 + progress * 0.5); // nở nhẹ
                double h = beam.getHeight() * 0.7; // làm mỏng để tạo cảm giác tia
                gc.setGlobalAlpha(alpha);
                gc.setFill(Color.web(trailColor, alpha));
                gc.fillRoundRect(p.getX() - w / 2, p.getY() - h / 2, w, h, w * 0.3, h * 0.3);
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    // Render shield
    private void renderShield(Shield shield) {
        if (shield != null) {
            shield.draw(gc);
        }
    }

    // Render explosion effects
    private void renderExplosions(List<ExplosionEffect> explosions) {
        for (ExplosionEffect explosion : explosions) {
            explosion.render(gc);
        }
    }
    
    // Render debris effects
    private void renderDebrisEffects(List<DebrisEffect> debrisEffects) {
        if (debrisEffects == null) return;
        for (DebrisEffect debris : debrisEffects) {
            if (!debris.isFinished()) {
                debris.render(gc);
            }
        }
    }

    // Setters

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void setBallImage(Image ballImage) {
        this.ballImage = ballImage;
    }

    public void setPaddleImage(Image paddleImage) {
        this.paddleImage = paddleImage;
    }

    public void setChargePulse(double chargePulse) {
        this.chargePulse = chargePulse;
    }
    
    public void setScreenShake(double offsetX, double offsetY) {
        this.shakeOffsetX = offsetX;
        this.shakeOffsetY = offsetY;
    }
    
    /**
     * Render intro animation khi bắt đầu level.
     * Hiển thị "LEVEL X" với hiệu ứng RGB split glitch và scan line.
     * 
     * @param progress Tiến độ animation (0.0 - 1.0)
     * @param currentLevel Số level hiện tại
     * @param renderGameState Callback để render game state (có thể null)
     */
    public void renderIntroAnimation(double progress, int currentLevel, Runnable renderGameState) {
        if (canvas == null) return;
        
        double t = Math.min(1.0, progress);
        
        // Render game state first
        if (renderGameState != null) {
            renderGameState.run();
        }
        
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // Scan line effect
        double scanHeight = h * 0.15;
        double scanY = -scanHeight + (h + scanHeight) * t;

        gc.setFill(Color.rgb(0, 255, 255, 0.20 * (1 - t)));
        gc.fillRect(0, scanY, w, scanHeight);

        // Fade overlay
        if (t < 0.6) {
            double fade = (0.6 - t) / 0.6;
            gc.setFill(Color.rgb(0, 0, 0, fade * 0.85));
            gc.fillRect(0, 0, w, h);
        }

        // Level text
        String txt = "LEVEL " + currentLevel;
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.EXTRA_BOLD, 70));

        javafx.scene.text.Text temp = new javafx.scene.text.Text(txt);
        temp.setFont(gc.getFont());
        double textW = temp.getLayoutBounds().getWidth();
        double textH = temp.getLayoutBounds().getHeight();
        double x = w / 2 - textW / 2;
        double y = h / 2 + textH / 4;

        double textAlpha = Math.min(1, t * 2);

        // RGB split glitch
        double offset = (Math.random() - 0.5) * (1 - t) * 20;

        gc.setGlobalAlpha(textAlpha);

        gc.setFill(Color.rgb(255, 60, 60));   // red ghost
        gc.fillText(txt, x + offset, y);

        gc.setFill(Color.rgb(60, 255, 255)); // cyan ghost
        gc.fillText(txt, x - offset * 0.7, y);

        gc.setFill(Color.WHITE);             // main text
        gc.fillText(txt, x, y);

        gc.setGlobalAlpha(1.0);
    }
    
    /**
     * Render level clear animation khi hoàn thành level.
     * Hiển thị hiệu ứng burst và fade to white.
     * 
     * @param progress Tiến độ animation (0.0 - 1.0)
     * @param renderGameState Callback để render game state (có thể null)
     */
    public void renderLevelClearAnimation(double progress, Runnable renderGameState) {
        if (canvas == null) return;
        
        double t = Math.min(1.0, progress);
        
        // Render game state first
        if (renderGameState != null) {
            renderGameState.run();
        }

        // Burst effect
        double burstAlpha = (1.0 - t);
        gc.setFill(Color.rgb(255, 240, 120, burstAlpha * 0.8));
        gc.fillOval(
                canvas.getWidth() / 2 - 250 * t,
                canvas.getHeight() / 2 - 250 * t,
                500 * t,
                500 * t
        );

        // Fade to white
        gc.setFill(Color.rgb(255, 255, 255, t * 0.65));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Render countdown animation (3, 2, 1, START!) trước khi bắt đầu chơi.
     * Hiển thị số đếm ngược với hiệu ứng RGB split, ring expansion và particles.
     * 
     * @param progress Tiến độ của từng số (0.0 - 1.0)
     * @param countdownNumber Số đang hiển thị (3, 2, 1, hoặc <1 cho "START!")
     * @param renderGameState Callback để render game state (có thể null)
     */
    public void renderCountdownAnimation(double progress, int countdownNumber, Runnable renderGameState) {
        if (canvas == null) return;

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.clearRect(0, 0, w, h);
        
        // Render game state first
        if (renderGameState != null) {
            renderGameState.run();
        }

        double t = Math.min(1.0, progress);

        if (countdownNumber == 1) {
            t = Math.pow(t, 0.45); // slow-motion easing
        }

        // text 3 2 1 start
        String text = countdownNumber >= 1 ? String.valueOf(countdownNumber) : "START!";
        boolean isStart = countdownNumber < 1;

        // Font dynamic
        double baseSize = isStart ? 110 : 160;
        double scale = 1.0 + 0.28 * Math.sin(t * Math.PI);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.EXTRA_BOLD, baseSize * scale));

        javafx.scene.text.Text tmp = new javafx.scene.text.Text(text);
        tmp.setFont(gc.getFont());
        double tw = tmp.getLayoutBounds().getWidth();
        double th = tmp.getLayoutBounds().getHeight();

        double x = w / 2 - tw / 2;
        double y = h / 2 + th / 4;

        double split = (1 - t) * 28;
        gc.setGlobalAlpha(1.0);

        gc.setFill(Color.rgb(255, 70, 150, 0.75)); // magenta glow
        gc.fillText(text, x + split, y);

        gc.setFill(Color.rgb(70, 255, 255, 0.75)); // cyan glow
        gc.fillText(text, x - split, y);

        gc.setFill(Color.WHITE); // main
        gc.fillText(text, x, y);

        double ringAlpha = (1 - t) * 0.35;
        double r = 160 + 180 * t;
        gc.setFill(Color.rgb(0, 255, 255, ringAlpha));
        gc.fillOval(w / 2 - r, h / 2 - r, r * 2, r * 2);

        if (t < 0.15) {
            double warpA = (0.15 - t) / 0.15;
            gc.setFill(Color.rgb(0, 0, 0, warpA * 0.4));
            gc.fillRect(0, 0, w, h);

            double sx = 1 + warpA * 0.25;
            gc.save();
            gc.translate(w / 2, h / 2);
            gc.scale(sx, sx);
            gc.translate(-w / 2, -h / 2);
            gc.setFill(Color.WHITE);
            gc.fillText(text, x, y);
            gc.restore();
        }

        int particleCount = 22;
        double orbitR = 120 + 25 * Math.sin(t * Math.PI);
        for (int i = 0; i < particleCount; i++) {
            double a = (i / (double) particleCount) * 2 * Math.PI;
            double px = w / 2 + Math.cos(a + t * 4) * orbitR;
            double py = h / 2 + Math.sin(a + t * 4) * orbitR;

            gc.setFill(Color.rgb(0, 255, 255, 0.8 * (1 - t)));
            gc.fillOval(px - 4, py - 4, 8, 8);
        }

        if (isStart) {
            double tt = Math.min(1.0, t * 2.2);

            // SWEEP BAR chạy ngang qua text
            double sweepX = (w * 1.2) * tt - w * 0.1;
            gc.setFill(Color.rgb(0, 255, 255, 0.35 * (1 - tt)));
            gc.fillRect(sweepX - 140, 0, 140, h); // thanh sáng quét

            // Holo fade-out
            gc.setGlobalAlpha(1 - tt);
            gc.setFill(Color.WHITE);
            gc.fillText(text, x, y);

            gc.setGlobalAlpha(0.4 * (1 - tt));
            gc.setFill(Color.rgb(0, 255, 255));
            gc.fillText(text, x + 3, y);

            gc.setGlobalAlpha(1.0);
        }
    }


    // Getters
    
    /**
     * Lấy GraphicsContext để vẽ trực tiếp (dùng cho các trường hợp đặc biệt).
     * 
     * @return GraphicsContext của canvas
     */
    public GraphicsContext getGraphicsContext() {
        return gc;
    }
}
