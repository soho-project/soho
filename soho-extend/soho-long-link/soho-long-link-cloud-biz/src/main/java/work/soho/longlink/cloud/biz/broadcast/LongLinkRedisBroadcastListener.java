package work.soho.longlink.cloud.biz.broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.sender.Sender;
import work.soho.longlink.cloud.api.message.LongLinkBroadcastMessage;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class LongLinkRedisBroadcastListener implements MessageListener {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Sender sender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            LongLinkBroadcastMessage data = OBJECT_MAPPER.readValue(payload, LongLinkBroadcastMessage.class);
            if (data == null || data.getAction() == null) {
                return;
            }
            switch (data.getAction()) {
                case SEND_TO_UID:
                    sender.sendToUid(data.getUid(), data.getMsg());
                    break;
                case SEND_TO_CONNECT_ID:
                    sender.sendToConnectId(data.getConnectId(), data.getMsg());
                    break;
                case SEND_TO_ALL_UID:
                    sender.sendToAllUid(data.getMsg());
                    break;
                case SEND_TO_ALL_CONNECT:
                    sender.sendToAllConnect(data.getMsg());
                    break;
                case BIND_UID:
                    sender.bindUid(data.getConnectId(), data.getUid());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.warn("longlink redis broadcast parse failed: {}", payload, e);
        }
    }
}
