package cc.pineclone.eventflow.plugin.api;

import cc.pineclone.eventflow.plugin.api.template.ComponentTemplate;

import java.util.Optional;

public interface ComponentTemplateProvider<T extends ComponentTemplate<?, ?>> {

    Optional<T> findTemplate(String templateType);

}
