package work.soho.common.database.event;

import org.springframework.context.ApplicationEvent;

public class AfterSaveEvent<T> extends ApplicationEvent {
    private final T source;

    public AfterSaveEvent(Object publisher, T source) {
        super(publisher);
        this.source = source;
    }

    @Override
    public T getSource() {
        return source;
    }
}