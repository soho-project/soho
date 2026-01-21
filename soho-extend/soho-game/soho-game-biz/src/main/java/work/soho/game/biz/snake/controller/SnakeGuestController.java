package work.soho.game.biz.snake.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.security.utils.SecurityUtils;
import work.soho.game.biz.snake.SnakeGameService;
import work.soho.game.biz.snake.dto.RoomSnapshot;
import work.soho.game.biz.snake.model.GameRoomMode;

@Api(tags = "贪吃蛇游客接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/guest/snake")
public class SnakeGuestController {
    private final SnakeGameService snakeGameService;

    /**
     * 自动进入指定模式的房间。
     */
    @GetMapping("/autoJoin")
    public R<RoomSnapshot> autoJoin(@RequestParam(defaultValue = "ENDLESS") String mode,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String playerId) {
        String resolvedPlayerId = playerId;
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
}
