package listeners;

import core.Observer;
import events.BranchSummaryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// Listener 6: listens to the aggregator (second link of the chain) and keeps all summaries.
public class SummaryReporter implements Observer<BranchSummaryEvent> {
    private final ConcurrentLinkedQueue<BranchSummaryEvent> received = new ConcurrentLinkedQueue<>();

    public void onEvent(BranchSummaryEvent event) {
        received.add(event);
    }

    public int getSummaryCount() {
        return received.size();
    }

    public long getTotalCents() {
        long total = 0;
        for (BranchSummaryEvent summary : received) {
            total = total + summary.getTotalCents();
        }
        return total;
    }

    public List<BranchSummaryEvent> getSummaries() {
        return new ArrayList<>(received);
    }
}
