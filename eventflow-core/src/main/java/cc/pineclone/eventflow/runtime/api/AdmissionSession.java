package cc.pineclone.eventflow.runtime.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Deprecated
@Getter
@Setter
public final class AdmissionSession {
    private final UUID sessionId;
    private MutexProfile mutexProfile;
    private final Instant createdAt;
    private State state;

    public AdmissionSession(
            UUID sessionId,
            MutexProfile mutexProfile,
            Instant createdAt) {

        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.mutexProfile = Objects.requireNonNull(mutexProfile, "profile");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.state = State.IDLE;
    }

    public enum State {
        IDLE,  /* 当前会话处于原始状态（未入队、也未获得执行权） */
        QUEUED,  /*  */
        ADMITTED
    }
}
