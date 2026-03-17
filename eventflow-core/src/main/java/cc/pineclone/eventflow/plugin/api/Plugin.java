package cc.pineclone.eventflow.plugin.api;

public interface Plugin {

    String getPluginId();

    void registerComponentTemplate(ComponentTemplateRegistrar registrar);

}
