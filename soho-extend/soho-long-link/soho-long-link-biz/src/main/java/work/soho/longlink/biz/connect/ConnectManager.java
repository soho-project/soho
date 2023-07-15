package work.soho.longlink.biz.connect;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ConnectManager {
    /**
     * 添加连接
     * @param ctx
     */
    void addConnect(ChannelHandlerContext ctx);

    /**
     * 移除连接
     *
     * @param connectId
     */
    void removeConnect(String connectId);

    /**
     * 根据连接ID获取连接
     *
     * @param connectId
     * @return
     */
    ChannelHandlerContext getConnect(String connectId);

    /**
     * 获取所有的链接ID
     *
     * @return
     */
    Set<String> getAllConnectId();

    /**
     * 绑定用户连接
     *
     * @param connectId
     * @param uid
     */
    void bindUid(String connectId, String uid);

    /**
     * 解除连接用户绑定
     *
     * @param connectId
     * @param uid
     */
    void removeConnectIdFromUid(String connectId, String uid);

    /**
     * 获取指定用户ID所有连接
     *
     * @param uid
     * @return
     */
    HashSet<String> getConnectIdListByUid(String uid);

    /**
     * 获取所有用户ID
     *
     * @return
     */
    Set<String> getAllUid();
}
