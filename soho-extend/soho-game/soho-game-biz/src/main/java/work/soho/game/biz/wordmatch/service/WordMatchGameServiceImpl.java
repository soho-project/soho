package work.soho.game.biz.wordmatch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import work.soho.game.biz.wordmatch.board.WordMatchBoard;
import work.soho.game.biz.wordmatch.board.WordMatchBoardDelta;
import work.soho.game.biz.wordmatch.board.WordMatchBoardEngine;
import work.soho.game.biz.wordmatch.dto.AttackRequest;
import work.soho.game.biz.wordmatch.dto.GameOverResult;
import work.soho.game.biz.wordmatch.dto.MatchRequest;
import work.soho.game.biz.wordmatch.dto.RoomSnapshot;
import work.soho.game.biz.wordmatch.dto.SkillCastRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordRequest;
import work.soho.game.biz.wordmatch.dto.SubmitWordResponse;
import work.soho.game.biz.wordmatch.dto.WordMatchConfig;
import work.soho.game.biz.wordmatch.dto.WordDto;
import work.soho.game.biz.wordmatch.model.WordMatchPlayerState;
import work.soho.game.biz.wordmatch.model.WordMatchRoom;
import work.soho.game.biz.wordmatch.model.WordMatchRoomMode;
import work.soho.game.biz.wordmatch.model.WordMatchRoomPlayer;
import work.soho.game.biz.wordmatch.model.WordMatchRoundStatus;
import work.soho.game.biz.wordmatch.domain.WordMatchWord;
import work.soho.game.biz.wordmatch.service.WordMatchRankService.ResultStat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class WordMatchGameServiceImpl implements WordMatchGameService {
    private final Map<String, WordMatchRoom> rooms = new ConcurrentHashMap<>();
    private final Map<WordMatchRoomMode, Deque<MatchTicket>> matchQueues = new ConcurrentHashMap<>();
    private final WordMatchBattleService battleService;
    private final WordMatchBattleEventService eventService;
    private final WordValidationService wordValidationService;
    private final WordMatchRankService rankService;
    private final WordLibraryService wordLibraryService;
    /** 棋盘引擎（生成/匹配/消除/补齐） */
    private final WordMatchBoardEngine boardEngine = new WordMatchBoardEngine();
    private volatile WordMatchConfig config = new WordMatchConfig();
    /** 提示搜索分页大小 */
    private static final int HINT_PAGE_SIZE = 200;
    /** 最多扫描多少页词库 */
    private static final int HINT_MAX_PAGES = 5;
    /** 提示单词最小长度 */
    private static final int HINT_MIN_LENGTH = 2;
    /** 生成棋盘时最多重试次数 */
    private static final int BOARD_REGENERATE_MAX = 5;
    /** 初始嵌入单词数（最小/最大） */
    private static final int BOARD_EMBED_MIN = 3;
    private static final int BOARD_EMBED_MAX = 5;
    /** 提示随机源 */
    private static final SecureRandom HINT_RANDOM = new SecureRandom();

    @Override
    public RoomSnapshot autoMatch(MatchRequest request, String resolvedPlayerId) {
        WordMatchRoomMode mode = resolveMode(request != null ? request.getMode() : null);
        cleanupQueue(mode);
        if (mode == WordMatchRoomMode.SOLO) {
            WordMatchRoom room = createRoom(mode);
            rooms.put(room.getRoomId(), room);
            addPlayerToRoom(room, resolvedPlayerId, request != null ? request.getName() : null,
                    request != null ? request.getWordLevel() : null);
            return toSnapshot(room);
        }
        if (mode == WordMatchRoomMode.FOUR) {
            return matchGroup(request, resolvedPlayerId, 4);
        }
        return matchPair(request, resolvedPlayerId, mode);
    }

    @Override
    public RoomSnapshot joinRoom(String roomId, String playerId, String name) {
        if (!StringUtils.hasText(roomId) || !StringUtils.hasText(playerId)) {
            return null;
        }
        WordMatchRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        if (isPlayerInRoom(room, playerId)) {
            return toSnapshot(room);
        }
        // 对局进行中不允许中途加入
        if (room.getRound().getStatus() == WordMatchRoundStatus.RUNNING) {
            return null;
        }
        if (room.getPlayers().size() >= room.getMaxPlayers()) {
            return null;
        }
        addPlayerToRoom(room, playerId, name, null);
        return toSnapshot(room);
    }

    @Override
    public RoomSnapshot getRoomSnapshot(String roomId) {
        WordMatchRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        return toSnapshot(room);
    }

    @Override
    public List<List<String>> getBoardSnapshot(String roomId, String playerId) {
        WordMatchRoom room = rooms.get(roomId);
        if (room == null || !StringUtils.hasText(playerId)) {
            return null;
        }
        WordMatchBoard board = room.getBoardMap().get(playerId);
        if (board == null) {
            return null;
        }
        return boardEngine.snapshot(board);
    }

    @Override
    public boolean bindPlayerConnection(String roomId, String playerId, String uid, String connectId) {
        if (!StringUtils.hasText(roomId) || !StringUtils.hasText(playerId)) {
            return false;
        }
        WordMatchRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            if (playerId.equals(player.getPlayerId())) {
                player.setUid(uid);
                player.setConnectId(connectId);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updatePlayerWordLevel(String roomId, String playerId, String wordLevel) {
        if (!StringUtils.hasText(roomId) || !StringUtils.hasText(playerId) || !StringUtils.hasText(wordLevel)) {
            return false;
        }
        WordMatchRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            if (playerId.equals(player.getPlayerId())) {
                player.setWordLevel(wordLevel);
                room.getBoardMap().put(playerId, ensurePlayableBoard(wordLevel));
                return true;
            }
        }
        return false;
    }

    @Override
    public String hintWord(String roomId, String playerId, String wordLevel) {
        if (!StringUtils.hasText(roomId) || !StringUtils.hasText(playerId)) {
            return null;
        }
        WordMatchRoom room = rooms.get(roomId);
        if (room == null || room.getRound().getStatus() != WordMatchRoundStatus.RUNNING) {
            return null;
        }
        if (!isPlayerInRoom(room, playerId)) {
            return null;
        }
        WordMatchBoard board = room.getBoardMap().get(playerId);
        if (board == null) {
            return null;
        }
        String level = wordLevel;
        if (!StringUtils.hasText(level)) {
            level = resolvePlayerWordLevel(room, playerId);
        }
        return findHintForBoard(board, level);
    }

    @Override
    public SubmitWordResponse submitWord(SubmitWordRequest request) {
        SubmitWordResponse response = new SubmitWordResponse();
        if (request == null || !StringUtils.hasText(request.getRoomId()) || !StringUtils.hasText(request.getWord())) {
            response.setAccepted(false);
            response.setMessage("roomId and word required");
            return response;
        }
        WordMatchRoom room = rooms.get(request.getRoomId());
        if (room == null) {
            response.setAccepted(false);
            response.setMessage("room not found");
            return response;
        }
        // 回合未开始则拒绝
        if (room.getRound().getStatus() != WordMatchRoundStatus.RUNNING) {
            response.setAccepted(false);
            response.setMessage("round not running");
            return response;
        }
        String playerId = request.getPlayerId();
        if (!StringUtils.hasText(playerId)) {
            response.setAccepted(false);
            response.setMessage("playerId required");
            return response;
        }
        // 只有房间内玩家可以提交
        if (!isPlayerInRoom(room, playerId)) {
            response.setAccepted(false);
            response.setMessage("player not in room");
            return response;
        }
        String word = request.getWord().trim();
        String level = request.getWordLevel();
        if (!StringUtils.hasText(level)) {
            level = resolvePlayerWordLevel(room, playerId);
        }
        WordMatchWord matched = wordValidationService.validate(word, level);
        if (matched == null) {
            int combo = resetCombo(room, playerId);
            response.setAccepted(false);
            response.setScoreDelta(0);
            response.setCombo(combo);
            response.setMessage("invalid word");
            appendEvent(room, "WORD_CLEAR", buildWordEvent(request, response));
            return response;
        }
        // 确保玩家有棋盘
        WordMatchBoard board = room.getBoardMap().get(playerId);
        if (board == null) {
            board = boardEngine.createBoard(config.getBoardWidth(), config.getBoardHeight());
            room.getBoardMap().put(playerId, board);
        }
        // 棋盘内是否存在该单词路径
        WordMatchBoardDelta boardDelta = boardEngine.applyWord(board, word);
        if (boardDelta == null) {
            int combo = resetCombo(room, playerId);
            response.setAccepted(false);
            response.setScoreDelta(0);
            response.setCombo(combo);
            response.setMessage("word not on board");
            appendEvent(room, "WORD_CLEAR", buildWordEvent(request, response));
            return response;
        }
        // 若棋盘已无可消除单词，则尝试重生成棋盘
        if (findHintForBoard(board, level) == null) {
            WordMatchBoard regenerated = ensurePlayableBoard(level);
            room.getBoardMap().put(playerId, regenerated);
            boardDelta.setCleared(new java.util.ArrayList<>());
            boardDelta.setFilled(new java.util.ArrayList<>());
            boardDelta.setBoard(boardEngine.snapshot(regenerated));
        }
        int combo = increaseCombo(room, playerId);
        int delta = calculateScore(word, matched.getLevel(), combo);
        response.setAccepted(true);
        response.setScoreDelta(delta);
        response.setCombo(combo);
        response.setMessage("accepted");
        // 将棋盘变化塞到响应里，前端可直接渲染
        response.setBoardDelta(buildBoardDelta(boardDelta));
        room.getRound().getScores().put(playerId, room.getRound().getScores().getOrDefault(playerId, 0) + delta);
        appendEvent(room, "WORD_CLEAR", buildWordEvent(request, response));
        return response;
    }

    @Override
    public boolean castSkill(SkillCastRequest request) {
        if (request == null || !StringUtils.hasText(request.getRoomId()) || !StringUtils.hasText(request.getPlayerId())) {
            return false;
        }
        WordMatchRoom room = rooms.get(request.getRoomId());
        if (room == null || room.getRound().getStatus() != WordMatchRoundStatus.RUNNING) {
            return false;
        }
        if (!isPlayerInRoom(room, request.getPlayerId())) {
            return false;
        }
        appendEvent(room, "SKILL_CAST", request);
        return true;
    }

    @Override
    public boolean attack(AttackRequest request) {
        if (request == null || !StringUtils.hasText(request.getRoomId()) || !StringUtils.hasText(request.getFromPlayerId())) {
            return false;
        }
        WordMatchRoom room = rooms.get(request.getRoomId());
        if (room == null || room.getRound().getStatus() != WordMatchRoundStatus.RUNNING) {
            return false;
        }
        if (!isPlayerInRoom(room, request.getFromPlayerId())) {
            return false;
        }
        if (StringUtils.hasText(request.getTargetPlayerId()) && isPlayerInRoom(room, request.getTargetPlayerId())) {
            applyAttackPayload(room, request);
        }
        appendEvent(room, "ATTACK", request);
        return true;
    }

    @Override
    public boolean leaveRoom(String roomId, String playerId) {
        WordMatchRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }
        boolean removed = room.getPlayers().removeIf(p -> playerId.equals(p.getPlayerId()));
        if (removed) {
            room.getRound().getScores().remove(playerId);
            room.getComboMap().remove(playerId);
            room.getBoardMap().remove(playerId);
        }
        room.setUpdatedAt(Instant.now());
        if (room.getPlayers().isEmpty()) {
            rooms.remove(roomId);
        }
        return removed;
    }

    @Override
    public List<WordMatchRoom> listRooms() {
        return new ArrayList<>(rooms.values());
    }

    @Override
    public WordMatchRoom getRoomDetail(String roomId) {
        return rooms.get(roomId);
    }

    @Override
    public GameOverResult forceFinish(String roomId) {
        WordMatchRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        room.getRound().setStatus(WordMatchRoundStatus.GAME_OVER);
        room.getRound().setEndedAt(Instant.now());
        GameOverResult result = new GameOverResult();
        result.setRoomId(roomId);
        result.setReason("force_finish");
        result.setScores(room.getRound().getScores());
        result.setWinnerId(resolveWinner(room.getRound().getScores()));
        if (room.getBattleId() != null) {
            battleService.updateFinished(room.getBattleId(), "GAME_OVER", result.getWinnerId(), result.getReason(),
                    room.getRound().getScores(), LocalDateTime.now());
        }
        applyRankedEloIfNeeded(room, result);
        appendEvent(room, "GAME_OVER", result);
        return result;
    }

    @Override
    public boolean kickPlayer(String roomId, String playerId) {
        return leaveRoom(roomId, playerId);
    }

    @Override
    public WordMatchConfig getConfig() {
        return config;
    }

    @Override
    public WordMatchConfig updateConfig(WordMatchConfig config) {
        if (config != null) {
            this.config = config;
        }
        return this.config;
    }

    private WordMatchRoomMode resolveMode(String mode) {
        if (!StringUtils.hasText(mode)) {
            return WordMatchRoomMode.DUEL;
        }
        try {
            return WordMatchRoomMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WordMatchRoomMode.DUEL;
        }
    }

    private WordMatchRoom createRoom(WordMatchRoomMode mode) {
        WordMatchRoom room = new WordMatchRoom();
        room.setRoomId(UUID.randomUUID().toString());
        room.setMode(mode);
        room.setMaxPlayers(resolveMaxPlayers(mode));
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());
        if (battleService != null) {
            var battle = battleService.createBattle(room.getRoomId(), mode);
            if (battle != null) {
                room.setBattleId(battle.getId());
            }
        }
        return room;
    }

    private int resolveMaxPlayers(WordMatchRoomMode mode) {
        if (mode == null) {
            return config.getDuelMaxPlayers();
        }
        switch (mode) {
            case FOUR:
                return config.getFourMaxPlayers();
            case RANKED:
                return config.getRankedMaxPlayers();
            case AI:
                return config.getAiMaxPlayers();
            case SOLO:
                return 1;
            case DUEL:
            default:
                return config.getDuelMaxPlayers();
        }
    }

    private RoomSnapshot toSnapshot(WordMatchRoom room) {
        RoomSnapshot snapshot = new RoomSnapshot();
        snapshot.setRoomId(room.getRoomId());
        snapshot.setMode(room.getMode());
        snapshot.setStatus(room.getRound().getStatus());
        snapshot.setMaxPlayers(room.getMaxPlayers());
        List<String> players = new ArrayList<>();
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            players.add(player.getPlayerId());
        }
        snapshot.setPlayers(players);
        snapshot.setScores(room.getRound().getScores());
        return snapshot;
    }

    private RoomSnapshot matchPair(MatchRequest request, String playerId, WordMatchRoomMode mode) {
        Deque<MatchTicket> queue = matchQueues.computeIfAbsent(mode, key -> new ArrayDeque<>());
        MatchTicket opponent = findOpponent(queue, request);
        if (opponent != null) {
            WordMatchRoom room = createRoom(mode);
            rooms.put(room.getRoomId(), room);
            addPlayerToRoom(room, opponent.playerId, opponent.name, opponent.wordLevel);
            addPlayerToRoom(room, playerId, request != null ? request.getName() : null,
                    request != null ? request.getWordLevel() : null);
            return toSnapshot(room);
        }
        enqueue(queue, request, playerId);
        return null;
    }

    private RoomSnapshot matchGroup(MatchRequest request, String playerId, int groupSize) {
        Deque<MatchTicket> queue = matchQueues.computeIfAbsent(WordMatchRoomMode.FOUR, key -> new ArrayDeque<>());
        enqueue(queue, request, playerId);
        if (queue.size() < groupSize) {
            return null;
        }
        WordMatchRoom room = createRoom(WordMatchRoomMode.FOUR);
        rooms.put(room.getRoomId(), room);
        for (int i = 0; i < groupSize; i++) {
            MatchTicket ticket = queue.pollFirst();
            if (ticket == null) {
                break;
            }
            addPlayerToRoom(room, ticket.playerId, ticket.name, ticket.wordLevel);
        }
        return toSnapshot(room);
    }

    private void addPlayerToRoom(WordMatchRoom room, String playerId, String name, String wordLevel) {
        WordMatchRoomPlayer player = new WordMatchRoomPlayer();
        player.setPlayerId(playerId);
        player.setName(name);
        player.setWordLevel(wordLevel);
        player.setState(WordMatchPlayerState.IN_GAME);
        room.getPlayers().add(player);
        room.getRound().getScores().putIfAbsent(playerId, 0);
        room.getComboMap().putIfAbsent(playerId, 0);
        // 初始化个人棋盘（尽量保证可消除单词存在）
        room.getBoardMap().putIfAbsent(playerId, ensurePlayableBoard(wordLevel));
        room.setUpdatedAt(Instant.now());
        if (room.getPlayers().size() == room.getMaxPlayers()) {
            room.getRound().setStatus(WordMatchRoundStatus.RUNNING);
            room.getRound().setStartedAt(Instant.now());
            if (room.getBattleId() != null) {
                battleService.updateRunning(room.getBattleId(), LocalDateTime.now());
            }
            appendEvent(room, "MATCH_START", toSnapshot(room));
        }
    }

    private void enqueue(Deque<MatchTicket> queue, MatchRequest request, String playerId) {
        MatchTicket ticket = new MatchTicket();
        ticket.playerId = playerId;
        ticket.name = request != null ? request.getName() : null;
        ticket.wordLevel = request != null ? request.getWordLevel() : null;
        ticket.rankScore = request != null ? request.getRankScore() : null;
        ticket.requestedAtMillis = System.currentTimeMillis();
        queue.addLast(ticket);
    }

    private MatchTicket findOpponent(Deque<MatchTicket> queue, MatchRequest request) {
        if (queue.isEmpty()) {
            return null;
        }
        Integer rankScore = request != null ? request.getRankScore() : null;
        long now = System.currentTimeMillis();
        MatchTicket candidate = null;
        for (MatchTicket ticket : queue) {
            if (rankScore == null || ticket.rankScore == null) {
                candidate = ticket;
                break;
            }
            int range = resolveMatchRange(rankScore, now, ticket.requestedAtMillis);
            if (Math.abs(rankScore - ticket.rankScore) <= range) {
                candidate = ticket;
                break;
            }
        }
        if (candidate != null) {
            queue.remove(candidate);
        }
        return candidate;
    }

    private int resolveMatchRange(int rankScore, long now, long requestedAtMillis) {
        int base = 100;
        long waitMillis = Math.max(0L, now - requestedAtMillis);
        int expand = (int) (waitMillis / 10000L) * 50;
        return base + expand;
    }

    private void cleanupQueue(WordMatchRoomMode mode) {
        Deque<MatchTicket> queue = matchQueues.computeIfAbsent(mode, key -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        long timeout = Math.max(5, config.getMatchTimeoutSeconds()) * 1000L;
        queue.removeIf(ticket -> now - ticket.requestedAtMillis > timeout);
    }

    private static class MatchTicket {
        private String playerId;
        private String name;
        private String wordLevel;
        private Integer rankScore;
        private long requestedAtMillis;
    }

    private void appendEvent(WordMatchRoom room, String type, Object payload) {
        if (room == null || eventService == null) {
            return;
        }
        long seq = nextEventSeq(room);
        eventService.appendEvent(room.getBattleId(), room.getRoomId(), seq, type, payload);
    }

    private long nextEventSeq(WordMatchRoom room) {
        synchronized (room) {
            long seq = room.getEventSeq() + 1;
            room.setEventSeq(seq);
            return seq;
        }
    }

    private boolean isPlayerInRoom(WordMatchRoom room, String playerId) {
        if (room == null || playerId == null) {
            return false;
        }
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            if (playerId.equals(player.getPlayerId())) {
                return true;
            }
        }
        return false;
    }

    private String resolvePlayerWordLevel(WordMatchRoom room, String playerId) {
        if (room == null || playerId == null) {
            return null;
        }
        for (WordMatchRoomPlayer player : room.getPlayers()) {
            if (playerId.equals(player.getPlayerId())) {
                return player.getWordLevel();
            }
        }
        return null;
    }

    private java.util.Map<String, Object> buildWordEvent(SubmitWordRequest request, SubmitWordResponse response) {
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("request", request);
        payload.put("response", response);
        return payload;
    }

    private java.util.Map<String, Object> buildBoardDelta(WordMatchBoardDelta delta) {
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("cleared", delta.getCleared());
        payload.put("filled", delta.getFilled());
        payload.put("board", delta.getBoard());
        return payload;
    }

    private WordMatchBoard ensurePlayableBoard(String wordLevel) {
        List<String> seedWords = pickSeedWords(wordLevel);
        WordMatchBoard board = boardEngine.createBoardWithWords(config.getBoardWidth(), config.getBoardHeight(), seedWords);
        for (int i = 0; i < BOARD_REGENERATE_MAX; i++) {
            if (findHintForBoard(board, wordLevel) != null) {
                return board;
            }
            board = boardEngine.createBoardWithWords(config.getBoardWidth(), config.getBoardHeight(), seedWords);
        }
        return board;
    }

    private String findHintForBoard(WordMatchBoard board, String wordLevel) {
        String level = wordLevel;
        for (int page = 1; page <= HINT_MAX_PAGES; page++) {
            List<WordDto> words = wordLibraryService.listWords(level, page, HINT_PAGE_SIZE);
            if (words == null || words.isEmpty()) {
                if (page == 1 && StringUtils.hasText(level)) {
                    level = null;
                    words = wordLibraryService.listWords(level, page, HINT_PAGE_SIZE);
                }
                if (words == null || words.isEmpty()) {
                    break;
                }
            }
            Collections.shuffle(words, HINT_RANDOM);
            for (WordDto dto : words) {
                if (dto == null || !StringUtils.hasText(dto.getWord())) {
                    continue;
                }
                String word = dto.getWord().trim();
                if (word.length() < HINT_MIN_LENGTH) {
                    continue;
                }
                if (boardEngine.containsWord(board, word)) {
                    return word;
                }
            }
        }
        return null;
    }

    private List<String> pickSeedWords(String wordLevel) {
        List<String> candidates = new ArrayList<>();
        int target = BOARD_EMBED_MIN + HINT_RANDOM.nextInt(BOARD_EMBED_MAX - BOARD_EMBED_MIN + 1);
        String level = wordLevel;
        for (int page = 1; page <= HINT_MAX_PAGES && candidates.size() < target; page++) {
            List<WordDto> words = wordLibraryService.listWords(level, page, HINT_PAGE_SIZE);
            if (words == null || words.isEmpty()) {
                if (page == 1 && StringUtils.hasText(level)) {
                    level = null;
                    words = wordLibraryService.listWords(level, page, HINT_PAGE_SIZE);
                }
                if (words == null || words.isEmpty()) {
                    break;
                }
            }
            Collections.shuffle(words, HINT_RANDOM);
            for (WordDto dto : words) {
                if (dto == null || !StringUtils.hasText(dto.getWord())) {
                    continue;
                }
                String word = dto.getWord().trim();
                if (word.length() < HINT_MIN_LENGTH) {
                    continue;
                }
                candidates.add(word);
                if (candidates.size() >= target) {
                    break;
                }
            }
        }
        return candidates;
    }

    private void applyAttackPayload(WordMatchRoom room, AttackRequest request) {
        java.util.Map<String, Object> payload = request.getPayload();
        if (payload == null) {
            return;
        }
        String targetId = request.getTargetPlayerId();
        if (payload.containsKey("resetCombo")) {
            Object value = payload.get("resetCombo");
            boolean reset = value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString());
            if (reset) {
                resetCombo(room, targetId);
            }
        }
        if (payload.containsKey("setCombo")) {
            int combo = parseInt(payload.get("setCombo"), room.getComboMap().getOrDefault(targetId, 0));
            room.getComboMap().put(targetId, Math.max(0, combo));
        }
        if (payload.containsKey("scoreDelta")) {
            int delta = parseInt(payload.get("scoreDelta"), 0);
            if (delta != 0) {
                int current = room.getRound().getScores().getOrDefault(targetId, 0);
                room.getRound().getScores().put(targetId, current + delta);
            }
        }
    }

    private int parseInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void applyRankedEloIfNeeded(WordMatchRoom room, GameOverResult result) {
        if (room == null || room.getMode() != WordMatchRoomMode.RANKED) {
            return;
        }
        Map<String, Integer> scores = room.getRound().getScores();
        if (scores == null || scores.size() < 2) {
            return;
        }
        Map<String, Integer> currentRatings = rankService.getRankScores(scores.keySet(), config.getBaseRankScore());
        EloRatingService elo = new EloRatingService(config.getEloK());
        Map<String, Integer> newRatings = elo.calculate(scores, currentRatings);
        Map<String, ResultStat> stats = buildWinLoss(scores);
        rankService.applyEloResult(newRatings, stats);
        result.setWinnerId(resolveWinner(scores));
    }

    private Map<String, ResultStat> buildWinLoss(Map<String, Integer> scores) {
        Map<String, ResultStat> stats = new java.util.HashMap<>();
        String winner = resolveWinner(scores);
        for (String playerId : scores.keySet()) {
            ResultStat stat = new ResultStat();
            if (winner != null && winner.equals(playerId)) {
                stat.wins = 1;
                stat.losses = 0;
            } else {
                stat.wins = 0;
                stat.losses = 1;
            }
            stats.put(playerId, stat);
        }
        return stats;
    }

    private int increaseCombo(WordMatchRoom room, String playerId) {
        if (room == null || playerId == null) {
            return 0;
        }
        int combo = room.getComboMap().getOrDefault(playerId, 0) + 1;
        room.getComboMap().put(playerId, combo);
        return combo;
    }

    private int resetCombo(WordMatchRoom room, String playerId) {
        if (room == null || playerId == null) {
            return 0;
        }
        room.getComboMap().put(playerId, 0);
        return 0;
    }

    private int calculateScore(String word, String level, int combo) {
        // 基础分：字母长度 * 2，连击给额外奖励
        int base = Math.max(1, word != null ? word.length() : 1) * 2;
        double levelMultiplier = resolveLevelMultiplier(level);
        int comboBonus = Math.min(combo * 2, 10);
        return (int) Math.round(base * levelMultiplier) + comboBonus;
    }

    private double resolveLevelMultiplier(String level) {
        if (level == null) {
            return 1.0;
        }
        String normalized = level.trim().toUpperCase();
        if (normalized.contains("CET6")) {
            return 1.2;
        }
        if (normalized.contains("IELTS") || normalized.contains("TOEFL")) {
            return 1.5;
        }
        if (normalized.contains("CET4")) {
            return 1.0;
        }
        return 1.0;
    }

    private String resolveWinner(Map<String, Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return null;
        }
        String winner = null;
        int max = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            int score = entry.getValue() == null ? 0 : entry.getValue();
            if (score > max) {
                max = score;
                winner = entry.getKey();
            }
        }
        return winner;
    }
}
