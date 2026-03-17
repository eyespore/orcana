package cc.pineclone.automation.input;

public record MouseWheelScroll(Direction direction, int value) {

    private static class MouseWheelScrollBuilder {
        Direction direction;
        int value;

        MouseWheelScroll build() {
            return new MouseWheelScroll(direction, value);
        }
    }

    public MouseWheelScroll(Direction direction) {
        this(direction, 0);
    }

    // TODO: MouseWheelScroll序列化
//    public JSON.Object toJson() {
//        return new ObjectBuilder()
//                .put("direction", direction.name())
//                .put("value", value)
//                .build();
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MouseWheelScroll that = (MouseWheelScroll) o;

        if (value != that.value) return false;
        return direction == that.direction;
    }

    @Override
    public String toString() {
        if (value == 0) {
            return "scroll-" + direction.name().toLowerCase();
        } else {
            return "scroll-" + direction.name().toLowerCase() + ":" + value;
        }
    }

    public enum Direction {
        UP,
        DOWN,
    }

}
