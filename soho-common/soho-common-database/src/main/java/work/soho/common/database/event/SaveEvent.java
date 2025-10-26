package work.soho.common.database.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 保存前事件
 */
@Data
public class SaveEvent extends ApplicationEvent {
    private String entityType;
    private Map<Object, Object> oldEntities;
    private List<Object> entities; // 即将被删除的实体列表
    private String operation;
    private Instant eventTime;

    public SaveEvent(Object source) {
        super(source);
        this.eventTime = Instant.now();
    }

    public SaveEvent(Object source, String entityType, List<Object> entities, String operation) {
        super(source);
        this.entityType = entityType;
        this.entities = entities;
        this.operation = operation;
        this.eventTime = Instant.now();
    }
}