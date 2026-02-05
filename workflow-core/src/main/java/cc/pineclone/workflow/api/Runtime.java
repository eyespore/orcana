package cc.pineclone.workflow.api;

public interface Runtime {

    void launch();  /* 启动 Runtime */

    void suspend();  /* 挂起 Runtime */

    void resume();  /* 恢复 Runtime */

    void terminate();  /* 终止 Runtime */

    RuntimeStatus status();

}

