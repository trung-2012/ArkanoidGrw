package game.arkanoid.models;

import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.GameConstants;

import static game.arkanoid.utils.GameConstants.BALL_SPEED;

public class Ball {
    private Vector2D position;
    private Vector2D velocity;
    private double radius;

    // Constructor
    public Ball(Vector2D position, double radius) {
        this.position = position;
        this.radius = radius;
        // Default initial velocity: roughly at BALL_SPEED at a 45deg up-right direction
        double diag = BALL_SPEED / Math.sqrt(2.0);
        this.velocity = new Vector2D(diag, -diag);
    }

    // Update ball position based on its velocity
    public void update() {
        position.add(velocity);
    }

    // Reverse velocity components
    public void reverseVelocityX() {
        this.velocity.setX(-this.velocity.getX());
    }

    public void reverseVelocityY() {
        this.velocity.setY(-this.velocity.getY());
    }

    // Collision detection with walls (returns true when ball falls below bottom)
    public boolean collideWithWall(double screenWidth, double screenHeight) {
        boolean bottomHit = false;

        // Left wall
        if (position.getX() - radius <= 0) {
            position.setX(radius);
            reverseVelocityX();
        }

        // Right wall
        if (position.getX() + radius >= screenWidth) {
            position.setX(screenWidth - radius);
            reverseVelocityX();
        }

        // Top wall
        if (position.getY() - radius <= 0) {
            position.setY(radius);
            reverseVelocityY();
        }

        // Bottom (missed by paddle) -> signal life lost / ball out
        if (position.getY() - radius > screenHeight) {
            bottomHit = true;
        }

        return bottomHit;
    }

    // Circle-rectangle collision against paddle. Assumes paddle.position is center
    // of paddle.
    public boolean collideWith(Paddle paddle) {
        if (paddle == null)
            return false;

        double rx = paddle.getPosition().getX();
        double ry = paddle.getPosition().getY();
        double hw = paddle.getWidth() / 2.0;
        double hh = paddle.getHeight() / 2.0;

        double closestX = clamp(position.getX(), rx - hw, rx + hw);
        double closestY = clamp(position.getY(), ry - hh, ry + hh);

        double dx = position.getX() - closestX;
        double dy = position.getY() - closestY;

        double distanceSq = dx * dx + dy * dy;
        if (distanceSq <= radius * radius) {
            // Nudge ball out of paddle and reflect Y velocity
            position.setY(closestY - radius - 1);
            reverseVelocityY();

            // Tweak X velocity based on hit position along paddle
            double hitPos = (position.getX() - rx) / hw; // -1 .. 1
            double speed = Math.max(velocity.magnitude(), BALL_SPEED);
            double newVx = speed * hitPos * 0.8; // tune factor
            this.velocity.setX(newVx);
            // Recalculate vy so total speed stays similar and directed upwards
            double vy = -Math.abs(Math.sqrt(Math.max(0, speed * speed - newVx * newVx)));
            this.velocity.setY(vy);

            return true;
        }

        return false;
    }

    // Circle-rectangle collision against a brick. Assumes brick.position is
    // top-left corner.
    public boolean collideWith(Brick brick) {
        if (brick == null || brick.getDestroyed())
            return false;

        double bx = brick.getPosition().getX();
        double by = brick.getPosition().getY();
        double bw = GameConstants.BRICK_WIDTH;
        double bh = GameConstants.BRICK_HEIGHT;

        double closestX = clamp(position.getX(), bx, bx + bw);
        double closestY = clamp(position.getY(), by, by + bh);

        double dx = position.getX() - closestX;
        double dy = position.getY() - closestY;

        double distanceSq = dx * dx + dy * dy;
        if (distanceSq <= radius * radius) {
            // Decide whether to reflect X or Y based on penetration
            double overlapLeft = Math.abs(position.getX() - bx);
            double overlapRight = Math.abs(position.getX() - (bx + bw));
            double overlapTop = Math.abs(position.getY() - by);
            double overlapBottom = Math.abs(position.getY() - (by + bh));

            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
            if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                reverseVelocityX();
            } else {
                reverseVelocityY();
            }

            // Damage the brick
            brick.takeDamage();
            return true;
        }

        return false;
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    // Getters & Setters

    public Vector2D getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}