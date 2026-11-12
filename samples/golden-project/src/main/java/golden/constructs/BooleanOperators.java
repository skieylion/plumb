package golden.constructs;

public class BooleanOperators {

    public boolean andInIf(int x, int y) {
        if (x > 0 && y > 0) {
            return true;
        }
        return false;
    }

    public boolean orInIf(int x, int y) {
        if (x > 0 || y > 0) {
            return true;
        }
        return false;
    }

    public boolean chainedAnd(int a, int b, int c, int d) {
        return a > 0 && b > 0 && c > 0 && d > 0;
    }

    public boolean mixedAndOr(int a, int b, int c) {
        return (a > 0 && b > 0) || c > 0;
    }

    public boolean negation(boolean flag) {
        return !flag;
    }

    public boolean nonShortCircuit(boolean a, boolean b, boolean c) {
        return a & b | c;
    }

    public int ternary(int x) {
        return x > 0 ? x : -x;
    }

    public int nestedTernary(int x) {
        return x > 0 ? 1 : x < 0 ? -1 : 0;
    }

    public boolean conditionsInLoop(int[] values, int limit) {
        int i = 0;
        while (i < values.length && values[i] < limit) {
            i++;
        }
        return i == values.length;
    }

    public String ternaryWithAnd(String text) {
        return text != null && !text.isEmpty() ? text : "default";
    }
}
