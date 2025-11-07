import java.util.ArrayList;
import java.util.HashSet;

public class Layer {
    private java.util.List<Shape> shapes = new java.util.ArrayList<>();

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    /**
     * method removeCircles.
     */
    public void removeCircles() {
        shapes.removeIf(s -> s instanceof Circle);
    }

    /**
     * method removeDuplicates.
     */
    public void removeDuplicates() {
        HashSet<Shape> set = new HashSet<>();
        ArrayList<Shape> result = new ArrayList<>();

        for (Shape s : shapes) {
            if (!set.contains(s)) {
                set.add(s);
                result.add(s);
            }
        }

        shapes = result;
    }

    /**
     * method getInfo.
     * @return Info
     */
    public String getInfo() {
        StringBuilder sb = new StringBuilder("Layer of crazy shapes:\n");
        for (Shape s : shapes) {
            sb.append(s.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}
