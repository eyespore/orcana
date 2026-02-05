package cc.pineclone.workflow.api.trigger;

/**
 * 应当确保两个属性相同的 TriggerDefinition 的 Hash 校验是唯一的，来避免相同的 TriggerDefinition 却创建了两个
 * Trigger
 */
public interface TriggerDefinition {

    TriggerIdentity getIdentity();

    void setIdentity(TriggerIdentity identity);

}
