package cc.pineclone.eventflow.runtime.api.bundle;

public sealed interface RuntimeBundle permits
        CommandBundle,
        EventBundle,
        RootEventBundle {
}
