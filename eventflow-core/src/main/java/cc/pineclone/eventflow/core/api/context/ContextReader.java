package cc.pineclone.eventflow.core.api.context;

import cc.pineclone.eventflow.common.api.value.ValueBinding;

public interface ContextReader {

    Object read(ValueBinding ref);

}
