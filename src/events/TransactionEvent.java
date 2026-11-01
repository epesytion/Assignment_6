package events;

import core.Event;
import core.EventType;

// One money movement. Immutable: all fields are final and there are no setters.
public final class TransactionEvent implements Event {
    private final long id;
    private final EventType type;
    private final String accountId;
    private final String branchId;
    private final long amountCents;

    public TransactionEvent(long id, EventType type, String accountId, String branchId, long amountCents) {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amountCents);
        }
        this.id = id;
        this.type = type;
        this.accountId = accountId;
        this.branchId = branchId;
        this.amountCents = amountCents;
    }

    public long getId() {
        return id;
    }

    public EventType getType() {
        return type;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBranchId() {
        return branchId;
    }

    public long getAmountCents() {
        return amountCents;
    }
}
