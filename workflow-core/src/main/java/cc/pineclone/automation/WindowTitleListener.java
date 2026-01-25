package cc.pineclone.automation;

import java.util.function.Consumer;

public interface WindowTitleListener extends Consumer<String> {

    @Override
    void accept(String windowTitle);
}
