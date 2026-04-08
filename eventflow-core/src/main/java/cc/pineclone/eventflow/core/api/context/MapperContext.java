package cc.pineclone.eventflow.core.api.context;

public interface MapperContext {

    ContextControl control();

    ContextReader reader();

    ContextWriter writer();

}
