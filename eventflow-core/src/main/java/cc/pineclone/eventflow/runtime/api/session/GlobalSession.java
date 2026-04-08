package cc.pineclone.eventflow.runtime.api.session;

import java.util.Map;

public interface GlobalSession extends Session {

    Map<String, Object> vars();

}
