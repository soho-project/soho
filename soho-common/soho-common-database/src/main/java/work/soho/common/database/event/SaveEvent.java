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

    /**
     * 旧实体列表; saveOrUpdate 时可能会有数据
     */
    private Map<Object, Object> oldEntities;

    /**
     * 保存的实体列表
     */
    private List<Object> entities;

    /**
     * 事件发生时间
     */
    private Instant eventTime;

    public SaveEvent(Object source) {
        super(source);
        this.eventTime = Instant.now();
    }

    public SaveEvent(Object source, String entityType, List<Object> entities, String operation) {
        super(source);
        this.entityType = entityType;
        this.entities = entities;
        this.eventTime = Instant.now();
    }
}