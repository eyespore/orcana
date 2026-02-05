package cc.pineclone.workflow.trigger.jnativehook.api;

import cc.pineclone.interaction.api.InteractionSpec;
import cc.pineclone.interaction.api.ModifierConstraint;
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
