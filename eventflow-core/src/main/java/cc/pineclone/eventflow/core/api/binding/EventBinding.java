package cc.pineclone.eventflow.core.api.binding;

import cc.pineclone.eventflow.core.api.ComponentId;
import cc.pineclone.eventflow.core.api.FlowComponent;

import java.util.List;

public interface EventBinding extends FlowComponent {

    EventSelector selector();

    List<ComponentId> mappers();

}
