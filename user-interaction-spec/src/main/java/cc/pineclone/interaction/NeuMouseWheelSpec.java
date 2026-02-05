package cc.pineclone.interaction;

public record NeuMouseWheelSpec(
    ScrollDirection direction
) implements NeuSpec {
    public enum ScrollDirection {
        UP,
        DOWN
    }
}
