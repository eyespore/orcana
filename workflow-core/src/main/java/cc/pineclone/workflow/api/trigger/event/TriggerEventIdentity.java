package cc.pineclone.workflow.api.trigger.event;

public record TriggerEventIdentity(
        String domain,
        String name,
        String type
) {
}
