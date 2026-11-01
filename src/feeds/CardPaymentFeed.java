package feeds;

import core.BaseSubject;
import core.EventSequence;
import core.EventType;
import core.ListenerRegistry;
import events.TransactionEvent;

// Data source 1: card payments.
public class CardPaymentFeed extends BaseSubject<TransactionEvent> {
    private final EventSequence sequence;

    public CardPaymentFeed(ListenerRegistry<TransactionEvent> registry, EventSequence sequence) {
        super(registry);
        this.sequence = sequence;
    }

    public void pay(String accountId, String branchId, long amountCents) {
        publish(new TransactionEvent(sequence.nextId(), EventType.CARD_PAYMENT, accountId, branchId, amountCents));
    }
}
