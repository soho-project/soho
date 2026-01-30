package work.soho.game.biz.wordmatch.controller;

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
import work.soho.game.biz.wordmatch.dto.AttackRequest;
import work.soho.game.biz.wordmatch.dto.MatchRequest;
import work.soho.game.biz.wordmatch.dto.RoomSnapshot;
import work.soho.game.biz.wordmatch.dto.SkillCastRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordResponse;
import work.soho.game.biz.wordmatch.WordMatchWsService;
import work.soho.game.biz.wordmatch.service.WordMatchGameService;

@Api(tags = "单词消消消乐用户接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game/user/word-match")
public class WordMatchUserController {
    private final WordMatchGameService gameService;
    private final WordMatchWsService wsService;

    @GetMapping("/match/auto")
    public R<RoomSnapshot> autoMatch(@RequestParam(required = false) String mode,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String playerId,
                                     @RequestParam(required = false) String wordLevel,
                                     @AuthenticationPrincipal SohoUserDetails userDetails) {
        String resolvedPlayerId = resolvePlayerId(playerId, userDetails);
        if (resolvedPlayerId == null) {
            return R.error("login required");
        }
        MatchRequest request = new MatchRequest();
        request.setMode(mode);
        request.setName(name);
        request.setPlayerId(resolvedPlayerId);
        request.setWordLevel(wordLevel);
        RoomSnapshot snapshot = gameService.autoMatch(request, resolvedPlayerId);
        if (snapshot == null) {
            return R.error("matching");
        }
        return R.success(snapshot);
    }

    @GetMapping("/room")
    public R<RoomSnapshot> room(@RequestParam String roomId) {
        RoomSnapshot snapshot = gameService.getRoomSnapshot(roomId);
        if (snapshot == null) {
            return R.error("room not found");
        }
        return R.success(snapshot);
    }

    @PostMapping("/submitWord")
    public R<SubmitWordResponse> submitWord(@RequestBody SubmitWordRequest request) {
        SubmitWordResponse response = gameService.submitWord(request);
        if (response != null && response.isAccepted() && request != null) {
            wsService.broadcastScoreSync(request.getRoomId());
        }
        return R.success(response);
    }

    @PostMapping("/skill")
    public R<Boolean> castSkill(@RequestBody SkillCastRequest request) {
        return gameService.castSkill(request) ? R.success(true) : R.error("cast failed");
    }

    @PostMapping("/attack")
    public R<Boolean> attack(@RequestBody AttackRequest request) {
        return gameService.attack(request) ? R.success(true) : R.error("attack failed");
    }

    @GetMapping("/hint")
    public R<String> hint(@RequestParam String roomId,
                          @RequestParam(required = false) String playerId,
                          @RequestParam(required = false) String wordLevel,
                          @AuthenticationPrincipal SohoUserDetails userDetails) {
        String resolvedPlayerId = resolvePlayerId(playerId, userDetails);
        if (resolvedPlayerId == null) {
            return R.error("login required");
        }
        String word = gameService.hintWord(roomId, resolvedPlayerId, wordLevel);
        if (word == null) {
            return R.error("no hint");
        }
        return R.success(word);
    }

    @PostMapping("/leave")
    public R<Boolean> leave(@RequestParam String roomId,
                            @RequestParam String playerId) {
        return gameService.leaveRoom(roomId, playerId) ? R.success(true) : R.error("leave failed");
    }

    private String resolvePlayerId(String playerId, SohoUserDetails userDetails) {
        if (playerId != null && !playerId.isBlank()) {
            return playerId;
        }
        if (userDetails != null && userDetails.getId() != null) {
            return userDetails.getId().toString();
        }
        Long uid = SecurityUtils.getLoginUserId();
        return uid != null ? uid.toString() : null;
    }
}
