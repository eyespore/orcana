package cc.pineclone.eventflow.runtime.api.session;

import java.util.Map;
import java.util.Optional;

public interface PathValueAccessor {

    Optional<Object> get(Map<String, Object> source, String path);

}
