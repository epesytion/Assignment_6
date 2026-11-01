package events;

import core.Event;
import core.EventType;

// Aggregated data of one branch, published by BranchAggregator. Immutable.
public final class BranchSummaryEvent implements Event {
    private final long id;
    private final String branchId;
    private final int transactionCount;
    private final long totalCents;

    public BranchSummaryEvent(long id, String branchId, int transactionCount, long totalCents) {
        this.id = id;
        this.branchId = branchId;
        this.transactionCount = transactionCount;
        this.totalCents = totalCents;
    }

    public long getId() {
        return id;
    }

    public EventType getType() {
        return EventType.BRANCH_SUMMARY;
    }

    public String getBranchId() {
        return branchId;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public long getTotalCents() {
        return totalCents;
    }
}
