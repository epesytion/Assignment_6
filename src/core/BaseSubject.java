package core;

import java.util.concurrent.atomic.AtomicLong;

// Shared subject logic: subscribe, unsubscribe, publish. Feeds and aggregators extend it.
// Delivery is synchronous: publish() returns after all listeners of that type have handled the event.
public class BaseSubject<E extends Event> implements Subject<E> {
    private final ListenerRegistry<E> registry;
    private final AtomicLong publishedEvents = new AtomicLong();
    private final AtomicLong failedDeliveries = new AtomicLong();

    public BaseSubject(ListenerRegistry<E> registry) {
        this.registry = registry;
    }

    public void subscribe(EventType type, Observer<? super E> observer) {
        registry.add(type, observer);
    }

    public void unsubscribe(EventType type, Observer<? super E> observer) {
        registry.remove(type, observer);
    }

    public void publish(E event) {
        publishedEvents.incrementAndGet();
        for (Observer<? super E> observer : registry.listenersFor(event.getType())) {
            deliver(observer, event);
        }
    }

    // One broken listener must not stop the others.
    private void deliver(Observer<? super E> observer, E event) {
        try {
            observer.onEvent(event);
        } catch (RuntimeException e) {
            failedDeliveries.incrementAndGet();
        }
    }

    public long getPublishedEvents() {
        return publishedEvents.get();
    }

    public long getFailedDeliveries() {
        return failedDeliveries.get();
    }
}
