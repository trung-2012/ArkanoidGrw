package game.arkanoid.managers;

import game.arkanoid.models.*;
import game.arkanoid.utils.Vector2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class RenderManager {

    private Canvas canvas;
    private GraphicsContext gc;

    // Images
    private Image ballImage;
    private Image paddleImage;

    // Effect state
    private double chargePulse = 0.0;

    public RenderManager(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    // Render all game obj
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

        // Clear canvas

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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


    // Getters

    public GraphicsContext getGraphicsContext() {
        return gc;
    }
}
