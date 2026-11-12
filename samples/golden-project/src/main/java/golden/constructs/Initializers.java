package golden.constructs;

import java.util.HashMap;
import java.util.Map;

public class Initializers {

    private static final Map<String, Integer> PRIORITIES = new HashMap<>();
    private static final boolean DEBUG = Boolean.getBoolean("golden.debug");
    private static final int LIMIT = DEBUG ? 10 : 1000;

    static {
        PRIORITIES.put("low", 1);
        PRIORITIES.put("medium", 5);
        if (DEBUG) {
            PRIORITIES.put("debug", 0);
        }
    }

    private final int capacity;
    private final String label;

    {
        label = LIMIT > 100 ? "production" : "debug";
    }

    public Initializers(int capacity) {
        this.capacity = capacity > 0 ? capacity : LIMIT;
    }

    public Initializers() {
        this(LIMIT);
    }

    public int priorityOf(String name) {
        return PRIORITIES.getOrDefault(name, -1);
    }

    public String describe() {
        return label + ":" + capacity;
    }
}
