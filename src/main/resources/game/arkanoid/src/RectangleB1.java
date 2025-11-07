public class RectangleB1 extends ShapeB1 {
    protected double width;
    protected double length;

    public RectangleB1() {
    }

    public RectangleB1(double width, double length) {
        this.width = width;
        this.length = length;
    }

    /**
     * constructor with attribute.
     * @param width the width
     * @param length the length
     * @param color the color
     * @param filled the filled
     */
    public RectangleB1(double width, double length, String color, boolean filled) {
        super(color, filled);
        this.width = width;
        this.length = length;
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
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return (length + width) * 2;
    }

    @Override
    public String toString() {
        return "Rectangle[width=" + width + ",length=" + length + ",color="
                + getColor() + ",filled=" + isFilled() + "]";
    }
}
