package listeners;

import core.Observer;
import events.TransactionEvent;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;

// Listener 4: writes one line per transaction. PrintStream is thread-safe, so lines never mix.
public class AuditLog implements Observer<TransactionEvent> {
    private final PrintStream out;
    private final AtomicLong entries = new AtomicLong();

    public AuditLog(PrintStream out) {
        this.out = out;
    }

    public void onEvent(TransactionEvent event) {
        out.println("AUDIT #" + event.getId() + " " + event.getType() + " " + event.getAccountId()
                + " " + event.getBranchId() + " " + event.getAmountCents());
        entries.incrementAndGet();
    }

    public long getEntries() {
        return entries.get();
    }
}
