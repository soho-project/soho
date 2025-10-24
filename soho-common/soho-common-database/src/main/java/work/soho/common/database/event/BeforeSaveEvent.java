package work.soho.common.database.event;

import org.springframework.context.ApplicationEvent;

/**
 * 保存前事件
 */
public class BeforeSaveEvent<T> extends ApplicationEvent {
    private final T source;

    public BeforeSaveEvent(Object publisher, T source) {
        super(publisher);
        this.source = source;
    }

    @Override
    public T getSource() {
        return source;
    }
}