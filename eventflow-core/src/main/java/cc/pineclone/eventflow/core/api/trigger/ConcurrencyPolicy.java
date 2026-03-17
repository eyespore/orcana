package cc.pineclone.eventflow.core.api.trigger;

public enum ConcurrencyPolicy {

    ALLOW_PARALLEL,
    CANCEL_PREVIOUS,
    REJECT_NEW,
    TOGGLE

}
