package work.soho.longlink.biz.connect;

import lombok.Getter;

import java.util.concurrent.atomic.LongAdder;

@Getter
public class ConnectInfo {
    private final String connectId;
    private final String clientIp;
    private final Integer clientPort;
    private final String serverIp;
    private final Integer serverPort;
    private final long createdAtMillis;
    private final LongAdder receivedMessages = new LongAdder();
    private final LongAdder sentMessages = new LongAdder();

    public ConnectInfo(String connectId, String clientIp, Integer clientPort, String serverIp, Integer serverPort, long createdAtMillis) {
        this.connectId = connectId;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.createdAtMillis = createdAtMillis;
    }

    public void recordReceivedMessage() {
        receivedMessages.increment();
    }

    public void recordSentMessage() {
        sentMessages.increment();
    }

    public long getReceivedMessages() {
        return receivedMessages.sum();
    }

    public long getSentMessages() {
        return sentMessages.sum();
    }
}
