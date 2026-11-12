package golden.constructs;

import java.util.List;

/**
 * Fixture for line-counting metrics.
 * Every method mixes code, comments and blank lines differently.
 */
public class LocCases {

    public int compactBody(int x) { return x + 1; }

    public int withBlankLines(int x) {
        int a = x + 1;

        int b = a * 2;


        return a + b;
    }

    // Line comment before the method.
    public int withLineComments(int x) {
        // Leading comment inside the body.
        int a = x + 1; // Trailing comment on a code line.
        // Comment between statements.
        return a * 2;
    }

    /**
     * Javadoc attached to the method.
     *
     * @param x input value
     * @return input plus one
     */
    public int withJavadoc(int x) {
        return x + 1;
    }

    public int withBlockComment(int x) {
        /*
         * Multi-line block comment
         * spanning several lines.
         */
        int a = x /* inline block comment */ + 1;
        return a;
    }

    public String multilineStatement(List<String> parts) {
        return String.join(
                ", ",
                parts.stream()
                        .map(String::strip)
                        .filter(p -> !p.isEmpty())
                        .toList()
        );
    }

    public String textBlock() {
        return """
                first line

                third line
                """;
    }

    public int braceOnOwnLine(int x)
    {
        if (x > 0)
        {
            return x;
        }
        return -x;
    }

    public int commentedOutCode(int x) {
        // int old = x * 3;
        // return old;
        return x;
    }

    public int annotatedMethod(@Deprecated int x) {
        return x;
    }
}
