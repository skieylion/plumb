package golden.constructs;

public class Conditionals {

    public int singleIf(int x) {
        if (x > 0) {
            return x;
        }
        return 0;
    }

    public int ifElse(int x) {
        if (x > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public String elseIfChain(int score) {
        if (score >= 90) {
            return "A";
        } else if (score >= 75) {
            return "B";
        } else if (score >= 60) {
            return "C";
        } else {
            return "F";
        }
    }

    public int nestedIf(int x, int y) {
        if (x > 0) {
            if (y > 0) {
                return 1;
            }
            return 2;
        }
        return 3;
    }

    public int ifWithoutBraces(int x) {
        if (x < 0) return -x;
        return x;
    }

    public int sequentialIfs(int x) {
        int result = 0;
        if (x % 2 == 0) {
            result += 1;
        }
        if (x % 3 == 0) {
            result += 2;
        }
        if (x % 5 == 0) {
            result += 4;
        }
        return result;
    }
}
