package cc.pineclone.eventflow.config.api;

import cc.pineclone.eventflow.config.impl.DefaultPropsNormalizer;
import cc.pineclone.eventflow.config.impl.DefaultPropsViewer;
import cc.pineclone.eventflow.config.api.definition.ComponentDefinition;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class TemplateSession<D extends ComponentDefinition> {

    private final String basePath;

    private final PropsCoercer propsCoercer;
    private final PropsNormalizer propsNormalizer;
    private final PropsViewer propsViewer;

    private final D rawDefinition;

    public TemplateSession(PropsCoercer propsCoercer, String basePath, D def) {
        this.propsCoercer = Objects.requireNonNull(propsCoercer, "coercer");
        this.basePath = (basePath == null || basePath.isBlank()) ? rawDefinition().templateType() : basePath;
        this.rawDefinition = def;

        Objects.requireNonNull(def, "def");
        Map<String, Object> defProps = Objects.requireNonNull(def.properties(), "def.properties()");
        /* 使用 LinkedHashMap 确保迭代顺序一致 */
        Map<String, Object> props = new LinkedHashMap<>(defProps);

        this.propsNormalizer = new DefaultPropsNormalizer(props, this.basePath, this.propsCoercer);
        this.propsViewer = new DefaultPropsViewer(props, this.basePath, this.propsCoercer);
    }

    public PropsCoercer coercer() {
        return propsCoercer;
    }

    public String basePath() {
        return basePath;
    }

    public D rawDefinition() {
        return rawDefinition;
    }

    public PropsNormalizer propsNormalizer() {
        return propsNormalizer;
    }

    public PropsViewer propsViewer() {
        return propsViewer;
    }

    public Map<String, Object> freezeProps() {
        return propsViewer.freezeProps();
    }

    public String pathOf(String... segments) {
        StringBuilder sb = new StringBuilder(basePath);
        if (segments == null) return sb.toString();
        for (String seg : segments) {
            if (seg == null) continue;
            String s = seg.trim();
            if (s.isEmpty()) continue;
            sb.append('.').append(s);
        }
        return sb.toString();
    }
}
