package work.soho.game.biz.snake.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.game.biz.snake.SnakeGameService;
import work.soho.game.biz.snake.dto.AdminRoomDetail;
import work.soho.game.biz.snake.dto.AdminRoomSummary;
import work.soho.game.biz.snake.dto.AdminSnakeConfig;
import work.soho.game.biz.snake.dto.GrantReviveCardRequest;

import java.util.List;

@Api(tags = "贪吃蛇后台管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/admin/snake")
public class SnakeAdminController {
    private final SnakeGameService snakeGameService;

    /**
     * 房间列表
     */
    @GetMapping("/rooms")
    @Node(value = "snakeGame::roomList", name = "获取贪吃蛇房间列表")
    public R<List<AdminRoomSummary>> listRooms() {
        return R.success(snakeGameService.listRoomSummaries());
    }

    /**
     * 房间详情
     */
    @GetMapping("/rooms/{roomId}")
    @Node(value = "snakeGame::roomDetail", name = "获取贪吃蛇房间详情")
    public R<AdminRoomDetail> roomDetail(@PathVariable String roomId) {
        return R.success(snakeGameService.getRoomDetail(roomId));
    }

    /**
     * 强制结束本局
     */
    @PostMapping("/rooms/{roomId}/finish")
    @Node(value = "snakeGame::roomFinish", name = "强制结束贪吃蛇局")
    public R<Boolean> finishRoom(@PathVariable String roomId) {
        return R.success(snakeGameService.forceFinish(roomId));
    }

    /**
     * 踢出玩家
     */
    @PostMapping("/rooms/{roomId}/kick/{playerId}")
    @Node(value = "snakeGame::roomKick", name = "踢出贪吃蛇玩家")
    public R<Boolean> kickPlayer(@PathVariable String roomId, @PathVariable String playerId) {
        return R.success(snakeGameService.kickPlayer(roomId, playerId));
    }

    /**
     * 获取配置
     */
    @GetMapping("/config")
    @Node(value = "snakeGame::configGet", name = "获取贪吃蛇配置")
    public R<AdminSnakeConfig> getConfig() {
        return R.success(snakeGameService.getConfig());
    }

    /**
     * 更新配置
     */
    @PutMapping("/config")
    @Node(value = "snakeGame::configUpdate", name = "更新贪吃蛇配置")
    public R<AdminSnakeConfig> updateConfig(@RequestBody AdminSnakeConfig config) {
        snakeGameService.updateConfig(config.getBoostMultiplier(), config.getFoodMaxRatio());
        return R.success(snakeGameService.getConfig());
    }

    /**
     * 发放复活卡
     */
    @PostMapping("/reviveCards/grant")
    @Node(value = "snakeGame::reviveCardGrant", name = "发放贪吃蛇复活卡")
    public R<Integer> grantReviveCards(@RequestBody GrantReviveCardRequest request) {
        if (request == null || request.getRoomId() == null || request.getRoomId().isBlank()
                || request.getPlayerId() == null || request.getPlayerId().isBlank()) {
            return R.error("roomId/playerId required");
        }
        int count = request.getCount() == null ? 1 : request.getCount();
        Integer cards = snakeGameService.grantReviveCards(request.getRoomId(), request.getPlayerId(), count);
        if (cards == null) {
            return R.error("grant failed");
        }
        return R.success(cards);
    }
}
