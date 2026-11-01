

import core.EventSequence;
import core.EventType;
import core.ListenerRegistry;
import events.BranchSummaryEvent;
import events.TransactionEvent;
import feeds.CardPaymentFeed;
import feeds.TransferFeed;
import listeners.AuditLog;
import listeners.BalanceNotifier;
import listeners.BranchAggregator;
import listeners.FraudDetector;
import listeners.LimitMonitor;
import listeners.SummaryReporter;
import sim.ProducerPool;
import sim.TransactionProducer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

// THE composition root: the only place that creates objects and connects them.
// Everything else receives its collaborators through the constructor.
public class BankingApp {
    public static final List<String> ACCOUNT_IDS = List.of("ACC-0", "ACC-1", "ACC-2", "ACC-3");
    public static final List<String> BRANCH_IDS = List.of("BR-0", "BR-1");
    public static final long OPENING_BALANCE_CENTS = 100_000_000L;
    public static final long LIMIT_CENTS = 5_000_000L;
    public static final long FRAUD_THRESHOLD_CENTS = 15_000L;
    public static final int SUMMARY_BATCH_SIZE = 100;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 60;

    private final CardPaymentFeed cardFeed;
    private final TransferFeed transferFeed;
    private final BalanceNotifier balanceNotifier;
    private final LimitMonitor limitMonitor;
    private final FraudDetector fraudDetector;
    private final AuditLog auditLog;
    private final BranchAggregator branchAggregator;
    private final SummaryReporter summaryReporter;
    private final ProducerPool pool;

    public BankingApp(int threadCount, PrintStream auditOut) {
        EventSequence sequence = new EventSequence();

        cardFeed = new CardPaymentFeed(new ListenerRegistry<TransactionEvent>(), sequence);
        transferFeed = new TransferFeed(new ListenerRegistry<TransactionEvent>(), sequence);

        balanceNotifier = new BalanceNotifier(OPENING_BALANCE_CENTS, ACCOUNT_IDS);
        limitMonitor = new LimitMonitor(LIMIT_CENTS, ACCOUNT_IDS);
        fraudDetector = new FraudDetector(FRAUD_THRESHOLD_CENTS);
        auditLog = new AuditLog(auditOut);
        branchAggregator = new BranchAggregator(new ListenerRegistry<BranchSummaryEvent>(), sequence,
                BRANCH_IDS, SUMMARY_BATCH_SIZE);
        summaryReporter = new SummaryReporter();

        pool = new ProducerPool(Executors.newFixedThreadPool(threadCount), SHUTDOWN_TIMEOUT_SECONDS);

        subscribeListeners();
    }

    // Who listens to what.
    private void subscribeListeners() {
        cardFeed.subscribe(EventType.CARD_PAYMENT, balanceNotifier);
        cardFeed.subscribe(EventType.CARD_PAYMENT, limitMonitor);
        cardFeed.subscribe(EventType.CARD_PAYMENT, fraudDetector);
        cardFeed.subscribe(EventType.CARD_PAYMENT, auditLog);
        cardFeed.subscribe(EventType.CARD_PAYMENT, branchAggregator);

        transferFeed.subscribe(EventType.TRANSFER_DOMESTIC, balanceNotifier);
        transferFeed.subscribe(EventType.TRANSFER_DOMESTIC, limitMonitor);
        transferFeed.subscribe(EventType.TRANSFER_DOMESTIC, auditLog);
        transferFeed.subscribe(EventType.TRANSFER_DOMESTIC, branchAggregator);

        transferFeed.subscribe(EventType.TRANSFER_INTERNATIONAL, balanceNotifier);
        transferFeed.subscribe(EventType.TRANSFER_INTERNATIONAL, limitMonitor);
        transferFeed.subscribe(EventType.TRANSFER_INTERNATIONAL, fraudDetector);
        transferFeed.subscribe(EventType.TRANSFER_INTERNATIONAL, auditLog);
        transferFeed.subscribe(EventType.TRANSFER_INTERNATIONAL, branchAggregator);

        branchAggregator.subscribe(EventType.BRANCH_SUMMARY, summaryReporter);
    }

    // Starts the producers on the pool and returns after a graceful shutdown.
    public void runSimulation(int producerCount, int eventsPerProducer) throws InterruptedException {
        List<TransactionProducer> producers = new ArrayList<>();
        for (int i = 0; i < producerCount; i++) {
            producers.add(new TransactionProducer(cardFeed, transferFeed, ACCOUNT_IDS, BRANCH_IDS, eventsPerProducer));
        }
        pool.runAndShutdown(producers);
    }

    public CardPaymentFeed getCardFeed() {
        return cardFeed;
    }

    public TransferFeed getTransferFeed() {
        return transferFeed;
    }

    public BalanceNotifier getBalanceNotifier() {
        return balanceNotifier;
    }

    public LimitMonitor getLimitMonitor() {
        return limitMonitor;
    }

    public FraudDetector getFraudDetector() {
        return fraudDetector;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public BranchAggregator getBranchAggregator() {
        return branchAggregator;
    }

    public SummaryReporter getSummaryReporter() {
        return summaryReporter;
    }

    public ProducerPool getPool() {
        return pool;
    }
}
