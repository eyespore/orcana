package cc.pineclone.workflow.api.action.ctx;

public interface ActionContext {

    GlobalContext global();

    SessionContext session();

    CallContext call();

}
