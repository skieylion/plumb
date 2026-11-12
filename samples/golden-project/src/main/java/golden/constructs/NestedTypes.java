package golden.constructs;

import java.util.Comparator;

public class NestedTypes {

    private int counter;

    public static class StaticNested {

        public int clamp(int value, int min, int max) {
            if (value < min) {
                return min;
            }
            if (value > max) {
                return max;
            }
            return value;
        }
    }

    public class Inner {

        public int incrementIfPositive(int delta) {
            if (delta > 0) {
                counter += delta;
            }
            return counter;
        }
    }

    public Comparator<Integer> anonymousClass() {
        return new Comparator<>() {
            @Override
            public int compare(Integer a, Integer b) {
                if (a == null) {
                    return b == null ? 0 : -1;
                }
                return b == null ? 1 : a.compareTo(b);
            }
        };
    }

    public int localClass(int x) {
        class Doubler {
            int apply(int value) {
                if (value > 1000) {
                    return value;
                }
                return value * 2;
            }
        }
        Doubler doubler = new Doubler();
        if (x > 0) {
            return doubler.apply(x);
        }
        return 0;
    }

    public Runnable anonymousRunnable(int[] target) {
        return new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < target.length; i++) {
                    target[i]++;
                }
            }
        };
    }
}
