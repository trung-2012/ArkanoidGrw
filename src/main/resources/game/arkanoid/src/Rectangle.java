public class Rectangle extends Shape {
    protected Point topLeft;
    protected double width;
    protected double length;

    public Rectangle() {
    }

    public Rectangle(double width, double length) {
        this.width = width;
        this.length = length;
    }

    /**
     * constructor Rectangle.
     * @param width the width
     * @param length tthe length
     * @param color the color
     * @param filled the filled
     */
    public Rectangle(double width, double length, String color, boolean filled) {
        super(color, filled);
        this.width = width;
        this.length = length;
    }

    /**
     * constructor Rectangle.
     * @param topLeft the topLeft
     * @param width the width
     * @param length the length
     * @param color the collor
     * @param filled the filled
     */
    public Rectangle(Point topLeft, double width, double length, String color, boolean filled) {
        super(color, filled);
        this.topLeft = topLeft;
        this.width = width;
        this.length = length;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(Point topLeft) {
        this.topLeft = topLeft;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public double getArea() {
        return width * length;
    }

    @Override
    public double getPerimeter() {
        return 2 * (width + length);
    }

    @Override
    public String toString() {
        return "Rectangle[topLeft=" + topLeft + ",width=" + String.format("%.1f", width)
                + ",length=" + String.format("%.1f", length)
                + ",color=" + color + ",filled=" + filled + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rectangle)) {
            return false;
        }
        Rectangle r = (Rectangle) o;
        return this.topLeft.equals(r.topLeft)
                && Math.abs(this.width - r.width) < 0.001
                && Math.abs(this.length - r.length) < 0.001;
    }

    @Override
    public int hashCode() {
        return topLeft.hashCode() + (int) (width * 1000 + length * 1000);
    }
}
