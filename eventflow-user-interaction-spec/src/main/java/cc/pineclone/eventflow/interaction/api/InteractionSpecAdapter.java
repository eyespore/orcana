package cc.pineclone.eventflow.interaction.api;

import cc.pineclone.eventflow.interaction.exception.InteractionSpecAdapteeException;

/* 防腐层 */
@FunctionalInterface
public interface InteractionSpecAdapter<
        T extends InteractionSpec,
        E extends InteractionSpec> {

    /**
     * 将输入的 InteractionSpec 编码为目标平台的 InteractionSpec
     * @param t 输入的 InteractionSpec
     * @return 编码后的目标平台的 InteractionSpec
     */
    E adaptee(T t) throws InteractionSpecAdapteeException;

}
