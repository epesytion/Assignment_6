package core;

// A listener. It receives the whole event (push model).
public interface Observer<E extends Event> {
    void onEvent(E event);
}
