package cc.pineclone.eventflow.core.api.context;

public interface Session {

    SessionId id();

    ScopedVariables vars();

}
