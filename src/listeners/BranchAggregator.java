package listeners;

import core.BaseSubject;
import core.EventSequence;
import core.ListenerRegistry;
import core.Observer;
import events.BranchSummaryEvent;
import events.TransactionEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Listener 5 AND subject (chain of observers).
// It listens to transactions and republishes aggregated data: after every `batchSize`
// transactions of one branch it publishes one BranchSummaryEvent.
// Only the batch of ONE branch is locked, never the whole subject, and listeners are called outside the lock.
public class BranchAggregator extends BaseSubject<BranchSummaryEvent> implements Observer<TransactionEvent> {
    private final Map<String, BranchBatch> batches = new HashMap<>();   // filled in constructor, never changed

    public BranchAggregator(ListenerRegistry<BranchSummaryEvent> registry, EventSequence sequence,
                            Collection<String> branchIds, int batchSize) {
        super(registry);
        for (String branchId : branchIds) {
            batches.put(branchId, new BranchBatch(branchId, batchSize, sequence));
        }
    }

    public void onEvent(TransactionEvent event) {
        BranchBatch batch = batches.get(event.getBranchId());
        if (batch == null) {
            throw new IllegalArgumentException("unknown branch: " + event.getBranchId());
        }
        BranchSummaryEvent summary = batch.add(event.getAmountCents());
        if (summary != null) {
            publish(summary);
        }
    }

    // Transactions that are not yet part of a summary.
    public int getPendingTransactions() {
        int pending = 0;
        for (BranchBatch batch : batches.values()) {
            pending = pending + batch.getCount();
        }
        return pending;
    }

    public long getPendingCents() {
        long pending = 0;
        for (BranchBatch batch : batches.values()) {
            pending = pending + batch.getTotalCents();
        }
        return pending;
    }

    // Collects the transactions of one branch until the batch is full.
    private static class BranchBatch {
        private final String branchId;
        private final int batchSize;
        private final EventSequence sequence;
        private int count;
        private long totalCents;

        BranchBatch(String branchId, int batchSize, EventSequence sequence) {
            this.branchId = branchId;
            this.batchSize = batchSize;
            this.sequence = sequence;
        }

        // Returns a summary when the batch is full, otherwise null.
        synchronized BranchSummaryEvent add(long cents) {
            count++;
            totalCents = totalCents + cents;
            if (count < batchSize) {
                return null;
            }
            BranchSummaryEvent summary = new BranchSummaryEvent(sequence.nextId(), branchId, count, totalCents);
            count = 0;
            totalCents = 0;
            return summary;
        }

        synchronized int getCount() {
            return count;
        }

        synchronized long getTotalCents() {
            return totalCents;
        }
    }
}
