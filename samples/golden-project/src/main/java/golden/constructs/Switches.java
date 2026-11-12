package golden.constructs;

import java.time.DayOfWeek;

public class Switches {

    public String classicSwitch(int code) {
        String result;
        switch (code) {
            case 1:
                result = "one";
                break;
            case 2:
                result = "two";
                break;
            case 3:
                result = "three";
                break;
            default:
                result = "many";
        }
        return result;
    }

    public int fallThrough(int level) {
        int flags = 0;
        switch (level) {
            case 3:
                flags |= 4;
            case 2:
                flags |= 2;
            case 1:
                flags |= 1;
                break;
            default:
                flags = 0;
        }
        return flags;
    }

    public int switchWithoutDefault(int code) {
        int result = 0;
        switch (code) {
            case 10:
                result = 1;
                break;
            case 20:
                result = 2;
                break;
        }
        return result;
    }

    public int groupedClassicLabels(char c) {
        switch (c) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return 1;
            default:
                return 0;
        }
    }

    public boolean arrowSwitchStatement(DayOfWeek day) {
        boolean weekend = false;
        switch (day) {
            case SATURDAY, SUNDAY -> weekend = true;
            default -> weekend = false;
        }
        return weekend;
    }

    public int switchExpression(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY, SUNDAY -> 0;
        };
    }

    public int switchExpressionWithYield(String command) {
        return switch (command) {
            case "start" -> 1;
            case "stop" -> {
                int code = command.length();
                yield code * 10;
            }
            default -> {
                if (command.isEmpty()) {
                    yield -1;
                }
                yield 0;
            }
        };
    }

    public String patternSwitch(Object value) {
        return switch (value) {
            case null -> "null";
            case Integer i when i > 100 -> "big int";
            case Integer i -> "int " + i;
            case String s when s.isBlank() -> "blank string";
            case String s -> "string " + s;
            default -> "other";
        };
    }
}
