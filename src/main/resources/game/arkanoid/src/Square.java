public class Square extends Rectangle {

    public Square() {
    }

    public Square(double side) {
        super(side, side);
    }

    public Square(double side, String color, boolean filled) {
        super(side, side, color, filled);
    }

    public Square(Point topLeft, double side, String color, boolean filled) {
        super(topLeft, side, side, color, filled);
    }

    public double getSide() {
        return width;
    }

    public void setSide(double side) {
        this.width = side;
        this.length = side;
    }

    public void setWidth(double side) {
        setSide(side);
    }

    public void setLength(double side) {
        setSide(side);
    }

    @Override
    public String toString() {
        return "Square[topLeft=" + topLeft + ",side=" + String.format("%.1f", width)
                + ",color=" + color + ",filled=" + filled + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Square)) {
            return false;
        }
        Square s = (Square) o;
        return this.topLeft.equals(s.topLeft)
                && Math.abs(this.width - s.width) < 0.001;
    }

    @Override
    public int hashCode() {
        return topLeft.hashCode() + (int) (width * 1000);
    }
}
