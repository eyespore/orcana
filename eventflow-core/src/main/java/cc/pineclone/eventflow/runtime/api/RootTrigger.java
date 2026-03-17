package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.trigger.ConcurrencyPolicy;
import cc.pineclone.eventflow.core.api.trigger.Trigger;

import java.util.Set;

public interface RootTrigger {

    Trigger trigger();

    ConcurrencyPolicy concurrencyPolicy();

    Set<String> groups();

    default ComponentId id() {
        return trigger().id();
    }

}
