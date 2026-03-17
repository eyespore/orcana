package cc.pineclone.eventflow.interaction.api;

import cc.pineclone.eventflow.interaction.exception.ModifierConstraintAdapteeException;

@FunctionalInterface
public interface ModifierConstraintAdapter<
        M extends ModifierConstraint,
        N extends ModifierConstraint> {

    N adaptee(M m) throws ModifierConstraintAdapteeException;

}
