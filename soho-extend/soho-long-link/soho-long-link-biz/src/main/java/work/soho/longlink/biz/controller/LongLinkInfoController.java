package work.soho.longlink.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.core.result.SohoPage;
import work.soho.common.core.util.StringUtils;
import work.soho.longlink.biz.connect.ConnectInfo;
import work.soho.longlink.biz.connect.ConnectManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Api(tags = "长链接API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/longLink/admin/link")
public class LongLinkInfoController {
    /**
     * 连接管理
     */
    private final ConnectManager connectManager;

    /**
     * 当前服务连接列表
     *
     * @return
     */

    @ApiOperation(value = "当前服务连接列表")
    @GetMapping("/list")
    public R<SohoPage<LinkInfo>> list(
            @RequestParam(value = "uid", required = false) String uid,
            @RequestParam(value = "connectId", required = false) String connectId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        ArrayList<LinkInfo> list = new ArrayList<>();
        Iterator<String> uidIterator = connectManager.getAllUid().iterator();
        while(uidIterator.hasNext()) {
            String currentUid = uidIterator.next();
            if (!StringUtils.isEmpty(uid) && !currentUid.equals(uid)) {
                continue;
            }
            Set<String> connectIds = connectManager.getConnectIdListByUid(currentUid);
            if (connectIds == null) {
                continue;
            }
            Iterator<String> connectIdIterator = connectIds.iterator();
            while (connectIdIterator.hasNext()) {
                String currentConnectId = connectIdIterator.next();
                if (!StringUtils.isEmpty(connectId) && !currentConnectId.contains(connectId)) {
                    continue;
                }
                ConnectInfo connectInfo = connectManager.getConnectInfo(currentConnectId);
                list.add(LinkInfo.from(currentUid, currentConnectId, connectInfo));
            }
        }
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int total = list.size();
        int fromIndex = (safePage - 1) * safePageSize;
        int toIndex = Math.min(fromIndex + safePageSize, total);
        List<LinkInfo> pageList = fromIndex >= total ? new ArrayList<>() : list.subList(fromIndex, toIndex);
        return R.success(new SohoPage<>(pageList, total, safePageSize, safePage));
    }

    /**
     * 获取连接总数
     *
     * @return
     */
    @ApiOperation(value = "获取连接总数")
    @GetMapping(value = "/count")
    public R<Integer> getCount() {
        return R.success(connectManager.getAllConnectId().size());
    }

    @Getter
    @AllArgsConstructor
    private static class LinkInfo {
        private final String uid;
        private final String connectId;
        private final String clientIp;
        private final Integer clientPort;
        private final String serverIp;
        private final Integer serverPort;
        private final Long createdAtMillis;
        private final Long receivedMessages;
        private final Long sentMessages;

        private static LinkInfo from(String uid, String connectId, ConnectInfo connectInfo) {
            if (connectInfo == null) {
                return new LinkInfo(uid, connectId, null, null, null, null, null, 0L, 0L);
            }
            return new LinkInfo(
                    uid,
                    connectId,
                    connectInfo.getClientIp(),
                    connectInfo.getClientPort(),
                    connectInfo.getServerIp(),
                    connectInfo.getServerPort(),
                    connectInfo.getCreatedAtMillis(),
                    connectInfo.getReceivedMessages(),
                    connectInfo.getSentMessages()
            );
        }
    }
}
