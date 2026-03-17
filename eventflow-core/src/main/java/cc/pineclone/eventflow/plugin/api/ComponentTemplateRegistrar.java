package cc.pineclone.eventflow.plugin.api;

import cc.pineclone.eventflow.plugin.api.template.ComponentTemplate;

public interface ComponentTemplateRegistrar {

    void register(ComponentTemplate<?, ?> template);

}
