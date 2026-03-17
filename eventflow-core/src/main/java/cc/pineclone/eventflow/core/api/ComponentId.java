package cc.pineclone.eventflow.core.api;

import java.util.Objects;

public record ComponentId(String domain, String name ) {
    public ComponentId {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(name, "name");
        if (domain.isBlank()) throw new IllegalArgumentException("domain must not be blank");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
    }
}
