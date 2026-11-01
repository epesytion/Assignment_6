package core;

// Base contract of all events. Implementations must be immutable.
public interface Event {
    long getId();

    EventType getType();
}
