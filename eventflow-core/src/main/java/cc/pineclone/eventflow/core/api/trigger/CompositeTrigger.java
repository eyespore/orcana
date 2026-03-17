package cc.pineclone.eventflow.core.api.trigger;

import java.util.List;

public interface CompositeTrigger extends Trigger {

    List<Trigger> children();

    void addChild(Trigger trigger);

    void removeChild(Trigger trigger);

}
