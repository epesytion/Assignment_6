package listeners;

import core.Observer;
import events.TransactionEvent;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

// Listener 2: counts how many accounts crossed the spending limit. Each account is counted once.
public class LimitMonitor implements Observer<TransactionEvent> {
    private final long limitCents;
    private final KeyedTotals spentByAccount;
    private final AtomicLong breaches = new AtomicLong();

    public LimitMonitor(long limitCents, Collection<String> accountIds) {
        this.limitCents = limitCents;
        this.spentByAccount = new KeyedTotals(accountIds);
    }

    public void onEvent(TransactionEvent event) {
        long after = spentByAccount.add(event.getAccountId(), event.getAmountCents());
        long before = after - event.getAmountCents();
        if (before < limitCents && after >= limitCents) {
            breaches.incrementAndGet();
        }
    }

    public long getBreaches() {
        return breaches.get();
    }
}
