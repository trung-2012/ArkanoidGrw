public class Point {
    private double pointX;
    private double pointY;

    public Point(double pointX, double pointY) {
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public double getPointX() {
        return pointX;
    }

    public void setPointX(double pointX) {
        this.pointX = pointX;
    }

    public double getPointY() {
        return pointY;
    }

    public void setPointY(double pointY) {
        this.pointY = pointY;
    }

    /**
     * method distance.
     * @param other the other
     * @return distance
     */
    public double distance(Point other) {
        double dx = this.pointX - other.pointX;
        double dy = this.pointY - other.pointY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "(" + String.format("%.1f", pointX) + "," + String.format("%.1f", pointY) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point other = (Point) o;
        return Math.abs(this.pointX - other.pointX) < 0.001
                && Math.abs(this.pointY - other.pointY) < 0.001;
    }

    @Override
    public int hashCode() {
        return (int) (pointX * 1000 + pointY * 1000);
    }
}
