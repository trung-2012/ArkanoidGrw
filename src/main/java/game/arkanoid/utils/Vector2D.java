package game.arkanoid.utils;

import java.io.Serializable;

public class Vector2D implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double x;
    private double y;

    // Constructor
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Cộng hai vector
    public void add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
    }

    // Nhân vector với một hằng số
    public void multiply(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    // Tính độ dài của vector
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    // Chuẩn hóa vector về vector đơn vị.
    public void normalize() {
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
