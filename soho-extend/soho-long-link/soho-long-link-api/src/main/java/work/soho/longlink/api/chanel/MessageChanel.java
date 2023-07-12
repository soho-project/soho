package work.soho.longlink.api.chanel;

/**
 * 消息通道
 */
public interface MessageChanel {
    void onMessage(String message);

    void onMessage(String message, String connectId, String uid);
}
