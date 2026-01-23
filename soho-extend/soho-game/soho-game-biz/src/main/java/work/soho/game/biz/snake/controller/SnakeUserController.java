package work.soho.game.biz.snake.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.game.biz.snake.SnakeGameService;
import work.soho.game.biz.snake.dto.ExchangeReviveCardRequest;
import work.soho.game.biz.snake.dto.ReviveRequest;
import work.soho.game.biz.snake.dto.RoomSnapshot;
import work.soho.game.biz.snake.model.GameRoomMode;

@Api(tags = "贪吃蛇游客接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/user/snake")
public class SnakeUserController {
    /** 贪吃蛇服务 */
    private final SnakeGameService snakeGameService;

    /**
     * 自动进入指定模式的房间。
     */
    @GetMapping("/autoJoin")
    public R<RoomSnapshot> autoJoin(@RequestParam(defaultValue = "ENDLESS") String mode,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String playerId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        String resolvedPlayerId = sohoUserDetails.getId().toString();
        if (resolvedPlayerId == null || resolvedPlayerId.isBlank()) {
            Long uid = SecurityUtils.getLoginUserId();
            if (uid != null) {
                resolvedPlayerId = uid.toString();
            }
        }
        if (resolvedPlayerId == null || resolvedPlayerId.isBlank()) {
            return R.error("login required");
        }
        GameRoomMode roomMode;
        try {
            roomMode = GameRoomMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            roomMode = GameRoomMode.ENDLESS;
        }
        RoomSnapshot snapshot = snakeGameService.autoJoin(roomMode, resolvedPlayerId, resolvedPlayerId, name);
        if (snapshot == null) {
            return R.error("room is full");
        }
        return R.success(snapshot);
    }

    /**
     * 无尽模式复活。
     */
    @PostMapping("/revive")
    public R<Boolean> revive(@RequestBody ReviveRequest request, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        if (request == null || request.getRoomId() == null || request.getRoomId().isBlank()) {
            return R.error("roomId required");
        }
        String userId = sohoUserDetails.getId().toString();
        if (userId == null) {
            return R.error("login required");
        }
        boolean revived = snakeGameService.revivePlayer(request.getRoomId(), userId);
        return revived ? R.success(true) : R.error("revive failed");
    }

    /**
     * 用积分兑换复活卡。
     */
    @PostMapping("/reviveCard/exchange")
    public R<Integer> exchangeReviveCard(@RequestBody ExchangeReviveCardRequest request, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        if (request == null || request.getRoomId() == null || request.getRoomId().isBlank()) {
            return R.error("roomId required");
        }
        String userId = sohoUserDetails.getId().toString();
        if (userId == null) {
            return R.error("login required");
        }
        int count = request.getCount() == null ? 1 : request.getCount();
        Integer cards = snakeGameService.exchangeReviveCards(request.getRoomId(), userId, count);
        if (cards == null) {
            return R.error("exchange failed");
        }
        return R.success(cards);
    }

    private String requireLoginUserId() {
        Long uid = SecurityUtils.getLoginUserId();
        if (uid != null) {
            return uid.toString();
        }
        return null;
    }
}
