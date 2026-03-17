package cc.pineclone.eventflow.interaction;

public record NeuMouseWheelSpec(
    ScrollDirection direction
) implements NeuSpec {
    public enum ScrollDirection {
        UP,
        DOWN
    }
}
