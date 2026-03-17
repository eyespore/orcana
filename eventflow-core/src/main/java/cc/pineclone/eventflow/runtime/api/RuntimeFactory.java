package cc.pineclone.eventflow.runtime.api;

import cc.pineclone.eventflow.config.api.definition.RuntimeDefinition;

@Deprecated
public interface RuntimeFactory {

    Runtime createRuntime(RuntimeDefinition definition);

}
