package work.soho.common.data.queue.store;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import work.soho.common.data.queue.message.DelayedMessage;

/**
 * 黑洞存储
 *
 * 开发测试用；一般不用在正式环境
 */
@Component
@ConditionalOnProperty(value = "soho.delayed-queue.store.drive", havingValue = "black-hole")
public class BlackHoleStoreImpl implements StoreInterface{
    @Override
    public void push(DelayedMessage delayedMessage) {

    }

    @Override
    public DelayedMessage pop() {
        return null;
    }
}
