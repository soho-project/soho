package work.soho.game.biz.wordmatch.service;

import work.soho.game.biz.wordmatch.dto.AttackRequest;
import work.soho.game.biz.wordmatch.dto.GameOverResult;
import work.soho.game.biz.wordmatch.dto.MatchRequest;
import work.soho.game.biz.wordmatch.dto.RoomSnapshot;
import work.soho.game.biz.wordmatch.dto.SkillCastRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordResponse;
import work.soho.game.biz.wordmatch.dto.WordMatchConfig;
import work.soho.game.biz.wordmatch.model.WordMatchRoom;

import java.util.List;

public interface WordMatchGameService {
    RoomSnapshot autoMatch(MatchRequest request, String resolvedPlayerId);

    RoomSnapshot joinRoom(String roomId, String playerId, String name);

    RoomSnapshot getRoomSnapshot(String roomId);

    List<List<String>> getBoardSnapshot(String roomId, String playerId);

    boolean bindPlayerConnection(String roomId, String playerId, String uid, String connectId);

    boolean updatePlayerWordLevel(String roomId, String playerId, String wordLevel);

    String hintWord(String roomId, String playerId, String wordLevel);

    SubmitWordResponse submitWord(SubmitWordRequest request);

    boolean castSkill(SkillCastRequest request);

    boolean attack(AttackRequest request);

    boolean leaveRoom(String roomId, String playerId);

    List<WordMatchRoom> listRooms();

    WordMatchRoom getRoomDetail(String roomId);

    GameOverResult forceFinish(String roomId);

    boolean kickPlayer(String roomId, String playerId);

    WordMatchConfig getConfig();

    WordMatchConfig updateConfig(WordMatchConfig config);
}
