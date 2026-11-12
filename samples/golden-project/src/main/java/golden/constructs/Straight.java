package golden.constructs;

public class Straight {

    private final int base;

    public Straight(int base) {
        this.base = base;
    }

    public void empty() {
    }

    public int oneLiner() {
        return base;
    }

    public int sequence(int x) {
        int doubled = x * 2;
        int shifted = doubled + base;
        int squared = shifted * shifted;
        return squared - x;
    }

    public String concatenate(String left, String right) {
        String trimmedLeft = left.strip();
        String trimmedRight = right.strip();
        return trimmedLeft + "-" + trimmedRight;
    }

    public static int staticCall(int x) {
        return Math.abs(x) + Math.max(x, 0);
    }
}
