package cc.pineclone.eventflow.application.api;

import cc.pineclone.eventflow.config.api.definition.RuntimeDefinition;
import cc.pineclone.eventflow.runtime.api.RuntimeAssembly;

public interface RuntimeAssembler {

    RuntimeAssembly assemble(RuntimeDefinition definition);

}
