package cc.pineclone.eventflow.plugin.api;

import cc.pineclone.eventflow.plugin.api.template.TriggerTemplate;

@Deprecated
public interface TriggerPlugin extends Plugin {

    void registerTriggerTemplate(ComponentTemplateRegistrar<TriggerTemplate> registrar);

}
