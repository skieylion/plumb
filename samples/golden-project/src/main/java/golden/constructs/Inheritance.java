package golden.constructs;

public class Inheritance {

    public interface Shape {

        double area();

        default String size() {
            double area = area();
            if (area > 100) {
                return "large";
            }
            return area > 10 ? "medium" : "small";
        }
    }

    public abstract static class AbstractShape implements Shape {

        private final String name;

        protected AbstractShape(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name");
            }
            this.name = name;
        }

        public String name() {
            return name;
        }

        public abstract double perimeter();
    }

    public static class Circle extends AbstractShape {

        private final double radius;

        public Circle(double radius) {
            super("circle");
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }

        @Override
        public double perimeter() {
            return 2 * Math.PI * radius;
        }
    }

    public static class Square extends AbstractShape {

        private final double side;

        public Square(double side) {
            super("square");
            this.side = side;
        }

        @Override
        public double area() {
            return side * side;
        }

        @Override
        public double perimeter() {
            return 4 * side;
        }

        @Override
        public String size() {
            if (side <= 0) {
                return "degenerate";
            }
            return super.size();
        }
    }

    public sealed interface Vehicle permits Car, Truck {
    }

    public record Car(int seats) implements Vehicle {

        public Car {
            if (seats <= 0 || seats > 9) {
                throw new IllegalArgumentException("seats");
            }
        }
    }

    public record Truck(double payload) implements Vehicle {

        public boolean heavy() {
            return payload > 10_000;
        }
    }

    public int toll(Vehicle vehicle) {
        return switch (vehicle) {
            case Car car -> car.seats() > 5 ? 15 : 10;
            case Truck truck -> truck.heavy() ? 50 : 30;
        };
    }
}
