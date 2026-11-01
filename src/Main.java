

import core.EventType;

// Console demo. Optional arguments: threads producers eventsPerProducer (defaults: 3 3 6)
public class Main {

    public static void main(String[] args) throws InterruptedException {
        int threads = readArgument(args, 0, 3);
        int producers = readArgument(args, 1, 3);
        int events = readArgument(args, 2, 6);

        BankingApp app = new BankingApp(threads, System.out);

        System.out.println("===== PART 1: manual events, one thread =====");
        app.getCardFeed().pay("ACC-0", "BR-0", 2_500);
        app.getTransferFeed().send("ACC-1", "BR-1", 40_000, true);   // big international transfer -> fraud flag
        printState(app);

        System.out.println();
        System.out.println("===== PART 2: BalanceNotifier leaves at runtime =====");
        app.getCardFeed().unsubscribe(EventType.CARD_PAYMENT, app.getBalanceNotifier());
        app.getCardFeed().pay("ACC-0", "BR-0", 1_000);
        System.out.println("ACC-0 balance did not change: " + app.getBalanceNotifier().getBalanceCents("ACC-0"));
        app.getCardFeed().subscribe(EventType.CARD_PAYMENT, app.getBalanceNotifier());   // joins again
        app.getCardFeed().pay("ACC-0", "BR-0", 1_000);
        System.out.println("ACC-0 balance changes again: " + app.getBalanceNotifier().getBalanceCents("ACC-0"));

        System.out.println();
        System.out.println("===== PART 3: " + producers + " producers x " + events + " events on " + threads + " threads =====");
        app.runSimulation(producers, events);
        System.out.println("pool terminated: " + app.getPool().isTerminated());
        printState(app);
    }

    private static void printState(BankingApp app) {
        System.out.println("--- state ---");
        System.out.println("audit entries      : " + app.getAuditLog().getEntries());
        System.out.println("notifications sent : " + app.getBalanceNotifier().getNotificationsSent());
        System.out.println("fraud flags        : " + app.getFraudDetector().getFlaggedCount());
        System.out.println("limit breaches     : " + app.getLimitMonitor().getBreaches());
        System.out.println("branch summaries   : " + app.getSummaryReporter().getSummaryCount());
        System.out.println("pending in batches : " + app.getBranchAggregator().getPendingTransactions());
    }

    private static int readArgument(String[] args, int index, int defaultValue) {
        if (args.length > index) {
            return Integer.parseInt(args[index]);
        }
        return defaultValue;
    }
}
