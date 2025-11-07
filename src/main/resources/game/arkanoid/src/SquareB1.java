public class SquareB1 extends RectangleB1 {
    public SquareB1() {
    }

    public SquareB1(double side) {
        super(side, side);
    }

    public SquareB1(double side, String color, boolean filled) {
        super(side, side, color, filled);
    }

    public double getSide() {
        return super.getWidth();
    }

    public void setSide(double side) {
        super.setWidth(side);
        super.setLength(side);
    }


    @Override
    public void setWidth(double side) {
        super.setWidth(side);
        super.setLength(side);
    }

    @Override
    public void setLength(double side) {
        super.setLength(side);
        super.setWidth(side);
    }

    @Override
    public String toString() {
        return "Square[side=" + super.getWidth() + ",color="
                + super.getColor() + ",filled=" + super.isFilled() + "]";
    }
}