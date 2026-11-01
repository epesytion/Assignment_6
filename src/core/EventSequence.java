package core;

import java.util.concurrent.atomic.AtomicLong;

// Gives every event in the whole system a unique id, even when many threads publish.
public class EventSequence {
    private final AtomicLong last = new AtomicLong();

    public long nextId() {
        return last.incrementAndGet();
    }
}
