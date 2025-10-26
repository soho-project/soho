package work.soho.common.database.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.List;

// 批量删除前事件
@Data
public class DeleteEvent extends ApplicationEvent {
    private String entityType;
    private List<Object> entityIds;
    private List<Object> entities; // 即将被删除的实体列表
    private String operation;
    private Instant eventTime;

    public DeleteEvent(Object source, String entityType, List<Object> entityIds,
                       List<Object> entities, String operation) {
        super(source);
        this.entityType = entityType;
        this.entityIds = entityIds;
        this.entities = entities;
        this.operation = operation;
        this.eventTime = Instant.now();
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<Object> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Object> entityIds) {
        this.entityIds = entityIds;
    }

    public List<Object> getEntities() {
        return entities;
    }

    public void setEntities(List<Object> entities) {
        this.entities = entities;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }
}