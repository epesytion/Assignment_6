package core;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

// Thread-safe list of listeners for each event type, without any lock around the whole subject.
// The map is filled once in the constructor and never changes, so threads read it without locks.
// Each list is a CopyOnWriteArrayList: iterating gives a snapshot, so joining or leaving
// (even inside a handler) can never break a publish that is running at the same time.
public class ListenerRegistry<E extends Event> {
    private final Map<EventType, CopyOnWriteArrayList<Observer<? super E>>> listenersByType =
            new EnumMap<>(EventType.class);

    public ListenerRegistry() {
        for (EventType type : EventType.values()) {
            listenersByType.put(type, new CopyOnWriteArrayList<Observer<? super E>>());
        }
    }

    public void add(EventType type, Observer<? super E> observer) {
        Objects.requireNonNull(observer, "observer");
        listenersByType.get(type).addIfAbsent(observer);   // the same observer is never added twice
    }

    public void remove(EventType type, Observer<? super E> observer) {
        listenersByType.get(type).remove(observer);
    }

    // Iterating over the returned list works on a snapshot.
    public List<Observer<? super E>> listenersFor(EventType type) {
        return listenersByType.get(type);
    }
}
