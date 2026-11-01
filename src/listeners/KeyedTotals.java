package listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

// Thread-safe running total per key (account id or branch id).
// All keys are known up front. The map is filled in the constructor and never changes.
public class KeyedTotals {
    private final Map<String, AtomicLong> totals = new HashMap<>();

    public KeyedTotals(Collection<String> keys) {
        for (String key : keys) {
            totals.put(key, new AtomicLong());
        }
    }

    // Adds the amount and returns the new total of this key.
    public long add(String key, long cents) {
        return find(key).addAndGet(cents);
    }

    public long get(String key) {
        return find(key).get();
    }

    public long sum() {
        long sum = 0;
        for (AtomicLong total : totals.values()) {
            sum = sum + total.get();
        }
        return sum;
    }

    private AtomicLong find(String key) {
        AtomicLong total = totals.get(key);
        if (total == null) {
            throw new IllegalArgumentException("unknown key: " + key);
        }
        return total;
    }
}
