package work.soho.longlink.biz.metrics;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 统计信息
 */
@Component
public class LongLinkMetrics {
    // 当前统计桶的秒级时间戳
    private final AtomicLong currentSecond = new AtomicLong(-1);
    // 当前秒的新建/断开累计
    private final LongAdder currentNew = new LongAdder();
    private final LongAdder currentClose = new LongAdder();
    // 上一秒的新建/断开结果
    private final AtomicLong lastSecondNew = new AtomicLong(0);
    private final AtomicLong lastSecondClose = new AtomicLong(0);
    // 累计收/发消息数（进程内）
    private final LongAdder totalReceivedMessages = new LongAdder();
    private final LongAdder totalSentMessages = new LongAdder();

    // 记录新建连接
    public void recordNewConnection() {
        rotateIfNeeded(nowSecond());
        currentNew.increment();
    }

    // 记录断开连接
    public void recordCloseConnection() {
        rotateIfNeeded(nowSecond());
        currentClose.increment();
    }

    // 获取上一秒新建连接数
    public long getLastSecondNew() {
        rotateIfNeeded(nowSecond());
        return lastSecondNew.get();
    }

    // 获取上一秒断开连接数
    public long getLastSecondClose() {
        rotateIfNeeded(nowSecond());
        return lastSecondClose.get();
    }

    // 记录收到的业务消息数（不含 ping/pong）
    public void recordReceivedMessage() {
        totalReceivedMessages.increment();
    }

    // 记录发送的业务消息数
    public void recordSentMessage() {
        totalSentMessages.increment();
    }

    // 获取累计接收消息数
    public long getTotalReceivedMessages() {
        return totalReceivedMessages.sum();
    }

    // 获取累计发送消息数
    public long getTotalSentMessages() {
        return totalSentMessages.sum();
    }

    // 取当前秒时间戳
    private long nowSecond() {
        return System.currentTimeMillis() / 1000L;
    }

    // 按秒滚动统计桶，将上一秒数据冻结
    private void rotateIfNeeded(long nowSecond) {
        long sec = currentSecond.get();
        if (sec == nowSecond) {
            return;
        }
        synchronized (this) {
            sec = currentSecond.get();
            if (sec == nowSecond) {
                return;
            }
            if (sec == -1) {
                currentSecond.set(nowSecond);
                currentNew.sumThenReset();
                currentClose.sumThenReset();
                lastSecondNew.set(0);
                lastSecondClose.set(0);
                return;
            }
            long newCount = currentNew.sumThenReset();
            long closeCount = currentClose.sumThenReset();
            if (nowSecond - sec == 1) {
                lastSecondNew.set(newCount);
                lastSecondClose.set(closeCount);
            } else {
                lastSecondNew.set(0);
                lastSecondClose.set(0);
            }
            currentSecond.set(nowSecond);
        }
    }
}
