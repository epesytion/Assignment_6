package core;

// Every event has one type. Listeners subscribe to the types they care about.
public enum EventType {
    CARD_PAYMENT,
    TRANSFER_DOMESTIC,
    TRANSFER_INTERNATIONAL,
    BRANCH_SUMMARY
}
