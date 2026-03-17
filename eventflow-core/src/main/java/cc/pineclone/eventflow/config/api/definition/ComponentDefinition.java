package cc.pineclone.eventflow.config.api.definition;

import cc.pineclone.eventflow.core.api.ComponentId;

import java.util.Map;

/* 描述某个组件应该如何被创建 */
public interface ComponentDefinition {

    ComponentId identity();

    String templateType();

    Map<String, Object> properties();

}
