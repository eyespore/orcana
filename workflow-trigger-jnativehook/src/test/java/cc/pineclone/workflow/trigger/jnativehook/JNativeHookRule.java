package cc.pineclone.workflow.trigger.jnativehook;

import com.github.kwhat.jnativehook.GlobalScreen;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class JNativeHookRule implements TestRule {
    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    GlobalScreen.registerNativeHook();
                    statement.evaluate();
                } finally {
                    GlobalScreen.unregisterNativeHook();
                    GlobalScreen.setEventDispatcher(null);
                }
            }
        };
    }
}
