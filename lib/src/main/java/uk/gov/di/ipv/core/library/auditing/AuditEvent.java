package uk.gov.di.ipv.core.library.auditing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;

import java.time.Instant;

@ExcludeFromGeneratedCoverageReport
@Getter
public class AuditEvent {
    @JsonProperty private final long timestamp;

    @JsonProperty("event_name")
    private final AuditEventTypes eventName;

    @JsonProperty private final AuditExtensions extensions;

    @JsonProperty("component_id")
    private final String componentId;

    @JsonProperty private final AuditEventUser user;

    @JsonCreator
    public AuditEvent(
            @JsonProperty(value = "event_name", required = true) AuditEventTypes eventName,
            @JsonProperty(value = "component_id", required = false) String componentId,
            @JsonProperty(value = "user", required = false) AuditEventUser user,
            @JsonProperty(value = "extensions", required = false) AuditExtensions extensions) {
        this.timestamp = Instant.now().getEpochSecond();
        this.eventName = eventName;
        this.componentId = componentId;
        this.user = user;
        this.extensions = extensions;
    }

    public AuditEvent(AuditEventTypes eventName, String componentId, AuditEventUser user) {
        this(eventName, componentId, user, null);
    }
}
