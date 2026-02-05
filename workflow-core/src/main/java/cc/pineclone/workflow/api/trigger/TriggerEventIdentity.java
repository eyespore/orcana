package cc.pineclone.workflow.api.trigger;

public record TriggerEventIdentity(
        String domain,
        String name,
        String type
) {
}
