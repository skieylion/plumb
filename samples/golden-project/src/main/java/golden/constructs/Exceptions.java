package golden.constructs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Exceptions {

    public int singleCatch(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int multipleCatches(String text, int divisor) {
        try {
            return Integer.parseInt(text) / divisor;
        } catch (NumberFormatException e) {
            return -1;
        } catch (ArithmeticException e) {
            return -2;
        }
    }

    public int multiCatch(String text, int divisor) {
        try {
            return Integer.parseInt(text) / divisor;
        } catch (NumberFormatException | ArithmeticException e) {
            return -1;
        }
    }

    public int tryFinally(int[] counter) {
        try {
            return counter[0];
        } finally {
            counter[0]++;
        }
    }

    public String tryWithResources(String text) {
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            return reader.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    public void throwStatement(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("negative: " + value);
        }
    }

    public int nestedTry(String first, String second) {
        try {
            int a = Integer.parseInt(first);
            try {
                return a + Integer.parseInt(second);
            } catch (NumberFormatException inner) {
                return a;
            }
        } catch (NumberFormatException outer) {
            return 0;
        }
    }
}
