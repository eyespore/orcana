package cc.pineclone.interaction.api;

import cc.pineclone.interaction.exception.ModifierConstraintAdapteeException;

@FunctionalInterface
public interface ModifierConstraintAdapter<
        M extends ModifierConstraint,
        N extends ModifierConstraint> {

    N adaptee(M m) throws ModifierConstraintAdapteeException;

}
