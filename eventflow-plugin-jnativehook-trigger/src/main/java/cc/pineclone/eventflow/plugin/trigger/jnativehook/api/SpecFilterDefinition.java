package cc.pineclone.eventflow.plugin.trigger.jnativehook.api;

import cc.pineclone.eventflow.interaction.api.InteractionSpec;
import cc.pineclone.eventflow.interaction.api.ModifierConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecFilterDefinition<
        T extends InteractionSpec,
        M extends ModifierConstraint>  {

    protected T originalSpec;
    protected M originalModifier;

    public boolean validate() {
        return originalSpec != null && originalModifier != null;
    }

}
