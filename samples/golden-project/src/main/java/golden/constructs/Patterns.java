package golden.constructs;

public class Patterns {

    public record Point(int x, int y) {
    }

    public record Segment(Point from, Point to) {
    }

    public int instanceofPattern(Object value) {
        if (value instanceof String s) {
            return s.length();
        }
        return 0;
    }

    public int instanceofPatternWithCondition(Object value) {
        if (value instanceof String s && !s.isEmpty()) {
            return s.charAt(0);
        }
        return -1;
    }

    public int recordDeconstruction(Object value) {
        if (value instanceof Point(int x, int y)) {
            return x + y;
        }
        return 0;
    }

    public int nestedRecordPatternSwitch(Object value) {
        return switch (value) {
            case Segment(Point(var x1, var y1), Point(var x2, var y2)) when x1 == x2 -> Math.abs(y2 - y1);
            case Segment(Point from, Point to) -> Math.abs(to.x() - from.x()) + Math.abs(to.y() - from.y());
            case Point(int x, int y) -> x * y;
            default -> 0;
        };
    }
}
