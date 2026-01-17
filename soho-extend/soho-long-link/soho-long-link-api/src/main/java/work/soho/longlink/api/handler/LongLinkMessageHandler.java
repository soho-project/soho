package work.soho.longlink.api.handler;

import work.soho.longlink.api.message.LongLinkMessage;

/**
 * 通用长链接消息处理器
 */
public interface LongLinkMessageHandler {
    /**
     * 是否处理该消息
     *
     * @param message
     * @return
     */
    boolean supports(LongLinkMessage message);

    /**
     * 处理消息
     *
     * @param message
     * @param connectId
     * @param uid
     */
    void onMessage(LongLinkMessage message, String connectId, String uid);
}
