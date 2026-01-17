package work.soho.longlink.biz.router;

import work.soho.longlink.api.message.LongLinkMessage;

public interface MessageRouter {
    void route(LongLinkMessage message, String connectId, String uid);
}
