package cc.pineclone.automation.trigger;

import java.util.Objects;
import java.util.Set;

public class TriggerIdentityGroup {

    private final Set<TriggerIdentity> identities;

    private TriggerIdentityGroup(Set<TriggerIdentity> identities) {
        this.identities = identities;
    }

    public static TriggerIdentityGroup of(TriggerIdentity... identities) {
        return TriggerIdentityGroup.of(Set.of(identities));
    }

    public static TriggerIdentityGroup of(Set<TriggerIdentity> identities) {
        return new TriggerIdentityGroup(identities);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TriggerIdentityGroup)) return false;
        return Objects.equals(identities, ((TriggerIdentityGroup) obj).identities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identities);
    }
}
