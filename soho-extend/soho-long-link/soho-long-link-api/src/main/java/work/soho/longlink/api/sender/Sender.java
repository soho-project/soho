package work.soho.longlink.api.sender;

/**
 * 消息发送服务
 */
public interface Sender {
    /**
     * 发送消息到指定用户
     *
     * @param uid
     * @param msg
     */
    void sendToUid(String uid, String msg);

    /**
     * 发送消息到指定连接
     *
     * @param connectId
     * @param msg
     */
    void sendToConnectId(String connectId, String msg);

    /**
     * 发送消息到所有用户
     *
     * @param msg
     */
    void sendToAllUid(String msg);

    /**
     * 发送消息到所有连接
     *
     * @param msg
     */
    void sendToAllConnect(String msg);

    /**
     * 连接绑定用户ID
     *
     * @param connectId
     * @param uid
     */
    void bindUid(String connectId, String uid);
}
