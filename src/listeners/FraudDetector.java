package listeners;

import core.Observer;
import events.TransactionEvent;

import java.util.concurrent.atomic.AtomicLong;

// Listener 3: flags a transaction when the amount reaches the threshold.
public class FraudDetector implements Observer<TransactionEvent> {
    private final long thresholdCents;
    private final AtomicLong flagged = new AtomicLong();

    public FraudDetector(long thresholdCents) {
        this.thresholdCents = thresholdCents;
    }

    public void onEvent(TransactionEvent event) {
        if (event.getAmountCents() >= thresholdCents) {
            flagged.incrementAndGet();
        }
    }

    public long getFlaggedCount() {
        return flagged.get();
    }
}
