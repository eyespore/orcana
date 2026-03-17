package cc.pineclone.eventflow.core.api.binding;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.List;

public interface CommandBinding {

    CommandSelector selector();

    List<ComponentId> actions();

}
