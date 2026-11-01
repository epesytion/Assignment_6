package sim;

import feeds.CardPaymentFeed;
import feeds.TransferFeed;

import java.util.List;

// One producer thread's job: publish `eventCount` events into the two feeds.
// The pattern repeats every 3 events: card payment, domestic transfer, international transfer.
public class TransactionProducer implements Runnable {
    public static final long CARD_CENTS = 1_000;
    public static final long DOMESTIC_CENTS = 5_000;
    public static final long INTERNATIONAL_CENTS = 20_000;

    private final CardPaymentFeed cardFeed;
    private final TransferFeed transferFeed;
    private final List<String> accountIds;
    private final List<String> branchIds;
    private final int eventCount;

    public TransactionProducer(CardPaymentFeed cardFeed, TransferFeed transferFeed,
                               List<String> accountIds, List<String> branchIds, int eventCount) {
        this.cardFeed = cardFeed;
        this.transferFeed = transferFeed;
        this.accountIds = accountIds;
        this.branchIds = branchIds;
        this.eventCount = eventCount;
    }

    public void run() {
        for (int i = 0; i < eventCount; i++) {
            String account = accountIds.get(i % accountIds.size());
            String branch = branchIds.get(i % branchIds.size());
            if (i % 3 == 0) {
                cardFeed.pay(account, branch, CARD_CENTS);
            } else if (i % 3 == 1) {
                transferFeed.send(account, branch, DOMESTIC_CENTS, false);
            } else {
                transferFeed.send(account, branch, INTERNATIONAL_CENTS, true);
            }
        }
    }
}
