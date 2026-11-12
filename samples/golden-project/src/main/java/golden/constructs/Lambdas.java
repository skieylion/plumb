package golden.constructs;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambdas {

    public List<String> methodReference(List<String> values) {
        return values.stream()
                .map(String::strip)
                .toList();
    }

    public long expressionLambda(List<Integer> values) {
        return values.stream()
                .filter(v -> v > 0)
                .count();
    }

    public long lambdaWithAnd(List<Integer> values) {
        return values.stream()
                .filter(v -> v > 0 && v % 2 == 0)
                .count();
    }

    public List<String> blockLambdaWithIf(List<Integer> values) {
        return values.stream()
                .map(v -> {
                    if (v < 0) {
                        return "negative";
                    }
                    return "non-negative";
                })
                .toList();
    }

    public Function<Integer, Integer> returnedLambda(int threshold) {
        return v -> v > threshold ? v : threshold;
    }

    public Predicate<String> nestedLambdas(List<String> stopWords) {
        return word -> stopWords.stream().noneMatch(stop -> stop.equalsIgnoreCase(word));
    }

    public String optionalChain(Optional<String> value) {
        return value
                .filter(v -> !v.isBlank())
                .map(String::toUpperCase)
                .orElseGet(() -> "EMPTY");
    }

    public Comparator<String> comparatorLambda() {
        return (a, b) -> {
            if (a.length() != b.length()) {
                return Integer.compare(a.length(), b.length());
            }
            return a.compareTo(b);
        };
    }

    public int lambdaInsideBranch(boolean lazy, int value) {
        if (lazy) {
            Supplier<Integer> supplier = () -> value * 2;
            return supplier.get();
        }
        return value;
    }
}
