package feeds;

import core.BaseSubject;
import core.EventSequence;
import core.EventType;
import core.ListenerRegistry;
import events.TransactionEvent;

// Data source 2: bank transfers. It publishes two event types, domestic and international.
public class TransferFeed extends BaseSubject<TransactionEvent> {
    private final EventSequence sequence;

    public TransferFeed(ListenerRegistry<TransactionEvent> registry, EventSequence sequence) {
        super(registry);
        this.sequence = sequence;
    }

    public void send(String accountId, String branchId, long amountCents, boolean international) {
        EventType type = EventType.TRANSFER_DOMESTIC;
        if (international) {
            type = EventType.TRANSFER_INTERNATIONAL;
        }
        publish(new TransactionEvent(sequence.nextId(), type, accountId, branchId, amountCents));
    }
}
