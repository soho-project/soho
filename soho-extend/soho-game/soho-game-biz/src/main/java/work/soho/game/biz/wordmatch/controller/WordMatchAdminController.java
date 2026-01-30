package work.soho.game.biz.wordmatch.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.game.biz.wordmatch.dto.GameOverResult;
import work.soho.game.biz.wordmatch.dto.WordMatchConfig;
import work.soho.game.biz.wordmatch.model.WordMatchRoom;
import work.soho.game.biz.wordmatch.service.WordMatchGameService;

import java.util.List;

@Api(tags = "单词消消消乐管理接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/admin/word-match")
public class WordMatchAdminController {
    private final WordMatchGameService gameService;

    @GetMapping("/rooms")
    public R<List<WordMatchRoom>> rooms() {
        return R.success(gameService.listRooms());
    }

    @GetMapping("/rooms/{roomId}")
    public R<WordMatchRoom> roomDetail(@PathVariable String roomId) {
        WordMatchRoom room = gameService.getRoomDetail(roomId);
        if (room == null) {
            return R.error("room not found");
        }
        return R.success(room);
    }

    @PostMapping("/rooms/{roomId}/finish")
    public R<GameOverResult> forceFinish(@PathVariable String roomId) {
        GameOverResult result = gameService.forceFinish(roomId);
        if (result == null) {
            return R.error("room not found");
        }
        return R.success(result);
    }

    @PostMapping("/rooms/{roomId}/kick/{playerId}")
    public R<Boolean> kick(@PathVariable String roomId, @PathVariable String playerId) {
        return gameService.kickPlayer(roomId, playerId) ? R.success(true) : R.error("kick failed");
    }

    @GetMapping("/config")
    public R<WordMatchConfig> config() {
        return R.success(gameService.getConfig());
    }

    @PutMapping("/config")
    public R<WordMatchConfig> updateConfig(@RequestBody WordMatchConfig config) {
        return R.success(gameService.updateConfig(config));
    }
}
