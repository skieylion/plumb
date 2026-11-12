package golden.constructs;

import java.util.List;

public class Loops {

    public int forLoop(int n) {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i;
        }
        return sum;
    }

    public int forEachLoop(List<Integer> values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum;
    }

    public int whileLoop(int n) {
        int steps = 0;
        while (n > 1) {
            n /= 2;
            steps++;
        }
        return steps;
    }

    public int doWhileLoop(int n) {
        int digits = 0;
        do {
            n /= 10;
            digits++;
        } while (n != 0);
        return digits;
    }

    public int breakAndContinue(int[] values) {
        int sum = 0;
        for (int value : values) {
            if (value < 0) {
                continue;
            }
            if (value > 100) {
                break;
            }
            sum += value;
        }
        return sum;
    }

    public int nestedLoops(int rows, int cols) {
        int cells = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells++;
            }
        }
        return cells;
    }

    public int labeledBreak(int[][] grid, int target) {
        int found = -1;
        outer:
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == target) {
                    found = r;
                    break outer;
                }
            }
        }
        return found;
    }

    public int infiniteLoopWithReturn(int start) {
        int n = start;
        for (;;) {
            if (n > 1000) {
                return n;
            }
            n *= 2;
        }
    }
}
