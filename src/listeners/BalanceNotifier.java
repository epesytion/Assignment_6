package listeners;

import core.Observer;
import events.TransactionEvent;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

// Listener 1: keeps the balance of each account and "sends a notification" for every transaction.
public class BalanceNotifier implements Observer<TransactionEvent> {
    private final long openingBalanceCents;
    private final KeyedTotals spentByAccount;
    private final AtomicLong notificationsSent = new AtomicLong();

    public BalanceNotifier(long openingBalanceCents, Collection<String> accountIds) {
        this.openingBalanceCents = openingBalanceCents;
        this.spentByAccount = new KeyedTotals(accountIds);
    }

    public void onEvent(TransactionEvent event) {
        spentByAccount.add(event.getAccountId(), event.getAmountCents());
        notificationsSent.incrementAndGet();
    }

    public long getBalanceCents(String accountId) {
        return openingBalanceCents - spentByAccount.get(accountId);
    }

    public long getNotificationsSent() {
        return notificationsSent.get();
    }
}
