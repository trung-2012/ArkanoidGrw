package game.arkanoid.utils;

public class Vector2D {
    private double x;
    private double y;

    // Constructor
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Vector operations
    public void add(Vector2D other) {
        // Add another vector to this one
        this.x += other.x;
        this.y += other.y;
    }

    public void multiply(double scalar) {
        // Multiply vector by a scalar
        this.x *= scalar;
        this.y *= scalar;
    }

    public double magnitude() {
        // Calculate the magnitude of the vector
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        // Normalize the vector to unit length
        double mag = Math.sqrt(x * x + y * y);
        if (mag != 0) {
            this.x /= mag;
            this.y /= mag;
        }
    }

    // Getters & Setters

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
