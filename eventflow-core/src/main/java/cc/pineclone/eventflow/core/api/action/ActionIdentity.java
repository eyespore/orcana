package cc.pineclone.eventflow.core.api.action;

/* 全局唯一的 Action 定义指示 */
@Deprecated
public record ActionIdentity(String domain, String name ) {
    public ActionIdentity {
        if (!(validateString(domain) && validateString(name))) {
            throw new IllegalArgumentException("domain and name must not be null");
        }
    }
    private boolean validateString(String str) {
        return str != null && !str.isBlank();
    }
}
