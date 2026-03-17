package cc.pineclone.eventflow.interaction;

public record NeuMouseSpec(
    NeuMouseButton button
) implements NeuSpec {

    public enum NeuMouseButton {
        BUTTON_1,  /* 鼠标左键 */
        BUTTON_2,  /* 鼠标右键 */
        BUTTON_3,  /* 鼠标中键 */
        BUTTON_4,  /* 鼠标前侧键 */
        BUTTON_5;  /* 鼠标后侧键 */
    }

}
