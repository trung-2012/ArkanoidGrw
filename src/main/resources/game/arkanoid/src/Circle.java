public class Circle extends Shape {
    protected Point center;
    protected double radius;

    public Circle() {
    }

    public Circle(double radius) {
        this.radius = radius;
    }

    public Circle(double radius, String color, boolean filled) {
        super(color, filled);
        this.radius = radius;
    }

    /**
     * constructor circle.
     * @param center the center
     * @param radius the radius
     * @param color the color
     * @param filled the filled
     */
    public Circle(Point center, double radius, String color, boolean filled) {
        super(color, filled);
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return 3.14 * radius * radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * 3.14 * radius;
    }

    @Override
    public String toString() {
        return "Circle[center=" + center + ",radius=" + String.format("%.1f", radius)
                + ",color=" + color + ",filled=" + filled + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Circle)) {
            return false;
        }
        Circle c = (Circle) o;
        return this.center.equals(c.center)
                && Math.abs(this.radius - c.radius) < 0.001;
    }

    @Override
    public int hashCode() {
        return center.hashCode() + (int) (radius * 1000);
    }
}
