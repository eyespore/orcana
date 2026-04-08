package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.core.api.ConcurrencyPolicy;
import cc.pineclone.eventflow.core.api.Trigger;

import java.util.Set;

public interface RootTrigger {

    Trigger delegate();

    ConcurrencyPolicy concurrencyPolicy();

    Set<String> groups();

}
