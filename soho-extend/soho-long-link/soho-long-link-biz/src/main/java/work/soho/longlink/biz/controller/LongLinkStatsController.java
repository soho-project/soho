package work.soho.longlink.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.longlink.biz.connect.ConnectManager;
import work.soho.longlink.biz.metrics.LongLinkMetrics;

@Api(tags = "长链接统计API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/longLink/admin/stats")
public class LongLinkStatsController {
    private final ConnectManager connectManager;
    private final LongLinkMetrics metrics;

    @ApiOperation(value = "当前连接总数")
    @GetMapping("/connections/count")
    public R<Integer> getConnectionCount() {
        return R.success(connectManager.getAllConnectId().size());
    }

    @ApiOperation(value = "当前在线用户数")
    @GetMapping("/users/count")
    public R<Integer> getOnlineUserCount() {
        return R.success(connectManager.getAllUid().size());
    }

    @ApiOperation(value = "上一秒断开连接数")
    @GetMapping("/connections/closed/last-second")
    public R<Long> getLastSecondClosedCount() {
        return R.success(metrics.getLastSecondClose());
    }

    @ApiOperation(value = "上一秒新建连接数")
    @GetMapping("/connections/new/last-second")
    public R<Long> getLastSecondNewCount() {
        return R.success(metrics.getLastSecondNew());
    }

    @ApiOperation(value = "接收消息总数")
    @GetMapping("/messages/received/count")
    public R<Long> getReceivedMessageCount() {
        return R.success(metrics.getTotalReceivedMessages());
    }

    @ApiOperation(value = "发送消息总数")
    @GetMapping("/messages/sent/count")
    public R<Long> getSentMessageCount() {
        return R.success(metrics.getTotalSentMessages());
    }
}
