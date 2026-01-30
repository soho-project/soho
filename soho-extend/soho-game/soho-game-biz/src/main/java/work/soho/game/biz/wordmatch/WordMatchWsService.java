package work.soho.game.biz.wordmatch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.JacksonUtils;
import work.soho.game.biz.wordmatch.dto.MatchRequest;
import work.soho.game.biz.wordmatch.dto.RoomSnapshot;
import work.soho.game.biz.wordmatch.dto.SubmitWordRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordResponse;
import work.soho.game.biz.wordmatch.model.WordMatchRoom;
import work.soho.game.biz.wordmatch.model.WordMatchRoomPlayer;
import work.soho.game.biz.wordmatch.model.WordMatchRoundStatus;
import work.soho.game.biz.wordmatch.service.WordMatchGameService;
import work.soho.longlink.api.message.LongLinkMessage;
import work.soho.longlink.api.sender.Sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单词消消消乐 WS 服务：负责解析 LongLink 消息并驱动游戏服务。
 * 约定 namespace=soho-game, topic=word-match
 */
@Service
@RequiredArgsConstructor
public class WordMatchWsService {
    private static final String NAMESPACE = "soho-game";
    private static final String TOPIC = "word-match";

    private final WordMatchGameService gameService;
    private final Sender sender;
    private final Map<String, String> playerRoomIndex = new ConcurrentHashMap<>();

    /** WS 入口：按 type 分发逻辑 */
    public void handleMessage(LongLinkMessage message, String connectId, String uid) {
        if (message == null) {
            return;
        }
        String type = safeString(message.getType());
        Map<String, Object> payload = toMap(message.getPayload());
        if ("join".equalsIgnoreCase(type)) {
            handleJoin(payload, connectId, uid);
        } else if ("leave".equalsIgnoreCase(type)) {
            handleLeave(payload, connectId, uid);
        } else if ("submit".equalsIgnoreCase(type) || "submitWord".equalsIgnoreCase(type)) {
            handleSubmit(payload, connectId, uid);
        } else if ("hint".equalsIgnoreCase(type)) {
            handleHint(payload, connectId, uid);
        } else if ("sync".equalsIgnoreCase(type)) {
            handleSync(payload, connectId, uid);
        } else if ("ping".equalsIgnoreCase(type)) {
            handlePing(payload, connectId, uid);
        }
    }

    /** 主动广播房间分数快照（用于 HTTP 提交后通知） */
    public void broadcastScoreSync(String roomId) {
        if (roomId == null || roomId.isBlank()) {
            return;
        }
        WordMatchRoom room = gameService.getRoomDetail(roomId);
        if (room == null) {
            return;
        }
        Map<String, Object> scorePayload = new HashMap<>();
        scorePayload.put("roomId", room.getRoomId());
        scorePayload.put("scores", room.getRound().getScores());
        sendToRoom(room, buildMessage("SCORE_SYNC", scorePayload));
    }

    /** join：加入房间或发起自动匹配 */
    private void handleJoin(Map<String, Object> payload, String connectId, String uid) {
        String playerId = resolvePlayerId(payload, uid, connectId);
        if (playerId == null) {
            sendError(connectId, uid, "playerId required");
            return;
        }
        String roomId = safeString(payload.get("roomId"));
        String name = safeString(payload.get("name"));
        if (roomId == null) {
            MatchRequest request = new MatchRequest();
            request.setMode(safeString(payload.get("mode")));
            request.setName(name);
            request.setPlayerId(playerId);
            request.setWordLevel(safeString(payload.get("wordLevel")));
            RoomSnapshot snapshot = gameService.autoMatch(request, playerId);
            if (snapshot == null) {
                sendMessageToPlayer(playerId, uid, connectId, buildMessage("MATCHING", Map.of("status", "matching")));
                return;
            }
            roomId = snapshot.getRoomId();
        }
        WordMatchRoom room = gameService.getRoomDetail(roomId);
        if (room == null) {
            sendError(connectId, uid, "room not found");
            return;
        }
        RoomSnapshot snapshot = gameService.joinRoom(roomId, playerId, name);
        if (snapshot == null) {
            sendError(connectId, uid, "room full or join failed");
            return;
        }
        gameService.bindPlayerConnection(roomId, playerId, uid, connectId);
        String wordLevel = safeString(payload.get("wordLevel"));
        if (wordLevel != null) {
            gameService.updatePlayerWordLevel(roomId, playerId, wordLevel);
        }
        playerRoomIndex.put(playerId, roomId);
        sendMessageToPlayer(playerId, uid, connectId, buildMessage("ROOM_SYNC", snapshot));
        sendBoardsToPlayer(room, playerId, uid, connectId);
        broadcastBoardSync(room, playerId);
        if (room.getRound().getStatus() == WordMatchRoundStatus.RUNNING) {
            sendToRoom(room, buildMessage("MATCH_START", snapshot));
        }
    }

    /** leave：离开房间并清理索引 */
    private void handleLeave(Map<String, Object> payload, String connectId, String uid) {
        String playerId = resolvePlayerId(payload, uid, connectId);
        if (playerId == null) {
            return;
        }
        String roomId = safeString(payload.get("roomId"));
        if (roomId == null) {
            roomId = playerRoomIndex.get(playerId);
        }
        if (roomId == null) {
            return;
        }
        gameService.leaveRoom(roomId, playerId);
        playerRoomIndex.remove(playerId);
    }

    /** submit：提交单词，成功则广播 WORD_CLEAR */
    private void handleSubmit(Map<String, Object> payload, String connectId, String uid) {
        String playerId = resolvePlayerId(payload, uid, connectId);
        String roomId = safeString(payload.get("roomId"));
        if (playerId == null || roomId == null) {
            sendError(connectId, uid, "roomId and playerId required");
            return;
        }
        String word = safeString(payload.get("word"));
        if (word == null) {
            sendError(connectId, uid, "word required");
            return;
        }
        SubmitWordRequest request = new SubmitWordRequest();
        request.setRoomId(roomId);
        request.setPlayerId(playerId);
        request.setWord(word);
        request.setWordLevel(safeString(payload.get("wordLevel")));
        SubmitWordResponse response = gameService.submitWord(request);
        Map<String, Object> result = new HashMap<>();
        result.put("playerId", playerId);
        result.put("word", word);
        result.put("response", response);
        if (response.isAccepted()) {
            WordMatchRoom room = gameService.getRoomDetail(roomId);
            sendToRoom(room, buildMessage("WORD_CLEAR", result));
            if (room != null) {
                Map<String, Object> scorePayload = new HashMap<>();
                scorePayload.put("roomId", room.getRoomId());
                scorePayload.put("scores", room.getRound().getScores());
                sendToRoom(room, buildMessage("SCORE_SYNC", scorePayload));
            }
        } else {
            sendMessageToPlayer(playerId, uid, connectId, buildMessage("WORD_CLEAR", result));
        }
    }

    /** hint：请求提示单词，仅返回给请求者 */
    private void handleHint(Map<String, Object> payload, String connectId, String uid) {
        String playerId = resolvePlayerId(payload, uid, connectId);
        String roomId = safeString(payload.get("roomId"));
        if (playerId == null || roomId == null) {
            sendError(connectId, uid, "roomId and playerId required");
            return;
        }
        String wordLevel = safeString(payload.get("wordLevel"));
        String hint = gameService.hintWord(roomId, playerId, wordLevel);
        if (hint == null) {
            sendError(connectId, uid, "no hint");
            return;
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("playerId", playerId);
        resp.put("word", hint);
        sendMessageToPlayer(playerId, uid, connectId, buildMessage("HINT", resp));
    }

    /** sync：回传当前玩家可见棋盘（包含自己与对手） */
    private void handleSync(Map<String, Object> payload, String connectId, String uid) {
        String playerId = resolvePlayerId(payload, uid, connectId);
        String roomId = safeString(payload.get("roomId"));
        if (playerId == null || roomId == null) {
            return;
        }
        WordMatchRoom room = gameService.getRoomDetail(roomId);
        if (room == null) {
            return;
        }
        sendBoardsToPlayer(room, playerId, uid, connectId);
    }

    /** ping：原样回传时间戳 */
    private void handlePing(Map<String, Object> payload, String connectId, String uid) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("clientTime", payload.get("clientTime"));
        resp.put("serverTime", System.currentTimeMillis());
        sendMessageToPlayer(null, uid, connectId, buildMessage("pong", resp));
    }

    /** 向指定玩家发送房间内所有玩家的棋盘快照 */
    private void sendBoardsToPlayer(WordMatchRoom room, String playerId, String uid, String connectId) {
        if (room == null) {
            return;
        }
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            List<List<String>> board = gameService.getBoardSnapshot(room.getRoomId(), player.getPlayerId());
            if (board == null) {
                continue;
            }
            Map<String, Object> payload = new HashMap<>();
            payload.put("playerId", player.getPlayerId());
            payload.put("board", board);
            sendMessageToPlayer(playerId, uid, connectId, buildMessage("BOARD_SYNC", payload));
        }
    }

    /** 广播某个玩家棋盘的 BOARD_SYNC */
    private void broadcastBoardSync(WordMatchRoom room, String playerId) {
        List<List<String>> board = gameService.getBoardSnapshot(room.getRoomId(), playerId);
        if (board == null) {
            return;
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("playerId", playerId);
        payload.put("board", board);
        sendToRoom(room, buildMessage("BOARD_SYNC", payload));
    }

    /** 向房间内所有玩家推送消息 */
    private void sendToRoom(WordMatchRoom room, LongLinkMessage message) {
        if (room == null || room.getPlayers().isEmpty()) {
            return;
        }
        String json = JacksonUtils.toJson(message);
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            if (player.getUid() != null && !player.getUid().isBlank()) {
                sender.sendToUid(player.getUid(), json);
            } else if (player.getConnectId() != null && !player.getConnectId().isBlank()) {
                sender.sendToConnectId(player.getConnectId(), json);
            } else if (player.getPlayerId() != null && !player.getPlayerId().isBlank()) {
                sender.sendToUid(player.getPlayerId(), json);
            }
        }
    }

    /** 向单个玩家推送消息（优先 uid -> connectId -> playerId） */
    private void sendMessageToPlayer(String playerId, String uid, String connectId, LongLinkMessage message) {
        String json = JacksonUtils.toJson(message);
        if (uid != null && !uid.isBlank()) {
            sender.sendToUid(uid, json);
        } else if (connectId != null && !connectId.isBlank()) {
            sender.sendToConnectId(connectId, json);
        } else if (playerId != null && !playerId.isBlank()) {
            sender.sendToUid(playerId, json);
        }
    }

    /** 发送错误消息 */
    private void sendError(String connectId, String uid, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", message);
        sendMessageToPlayer(null, uid, connectId, buildMessage("ERROR", payload));
    }

    /** 构造标准 LongLink 消息 */
    private LongLinkMessage buildMessage(String type, Object payload) {
        LongLinkMessage message = new LongLinkMessage();
        message.setNamespace(NAMESPACE);
        message.setTopic(TOPIC);
        message.setType(type);
        message.setPayload(payload);
        return message;
    }

    /** 解析 playerId（payload > uid > connectId） */
    private String resolvePlayerId(Map<String, Object> payload, String uid, String connectId) {
        String playerId = safeString(payload.get("playerId"));
        if (playerId != null) {
            return playerId;
        }
        if (uid != null && !uid.isBlank()) {
            return uid;
        }
        if (connectId != null && !connectId.isBlank()) {
            return connectId;
        }
        return null;
    }

    /** 将 payload 安全转为 Map */
    private Map<String, Object> toMap(Object payload) {
        if (payload instanceof Map<?, ?>) {
            Map<String, Object> result = new HashMap<>();
            ((Map<?, ?>) payload).forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        }
        return new HashMap<>();
    }

    /** 安全转换为非空字符串 */
    private String safeString(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }
}
