package work.soho.common.database.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class BeforeDeleteEvent extends ApplicationEvent {
    private String entityType;
    private Object entityId;
    private Object entity; // 即将被删除的完整实体数据
    private String operation;
    private Instant eventTime;
    private Map<String, Object> additionalInfo;

    public BeforeDeleteEvent(Object source, String entityType, Object entityId,
                             Object entity, String operation) {
        super(source);
        this.entityType = entityType;
        this.entityId = entityId;
        this.entity = entity;
        this.operation = operation;
        this.eventTime = Instant.now();
        this.additionalInfo = new HashMap<>();
    }

    // getters and setters
    public String getEntityType() { return entityType; }
    public Object getEntityId() { return entityId; }
    public Object getEntity() { return entity; }
    public String getOperation() { return operation; }
    public Instant getEventTime() { return eventTime; }
    public Map<String, Object> getAdditionalInfo() { return additionalInfo; }

    public void addAdditionalInfo(String key, Object value) {
        this.additionalInfo.put(key, value);
    }
}