package cc.pineclone.automation;

import java.util.UUID;

//@Getter
//@ToString
public record AutomationJobEvent(
        UUID jobId,
        AutomationJob.AutomationStatus status,
        AutomationJob.JobExecutionStatus executionStatus
) {}
