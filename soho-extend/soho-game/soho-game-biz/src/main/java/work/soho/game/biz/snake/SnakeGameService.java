package work.soho.game.biz.snake;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.JacksonUtils;
import work.soho.game.biz.domain.GameSnakePlayerProfile;
import work.soho.game.biz.service.GameSnakePlayerProfileService;
import work.soho.game.biz.snake.dto.*;
import work.soho.game.biz.snake.model.*;
import work.soho.longlink.api.message.LongLinkMessage;
import work.soho.longlink.api.sender.Sender;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class SnakeGameService {
    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 60;
    private static final int INITIAL_LENGTH = 3;
    private static final int INITIAL_FOODS = 3;
    private static final int DEFAULT_POINTS_PER_SEGMENT = 100;
    private static final long TICK_MILLIS = 200L;
    private final Sender sender;

    @Value("${soho.game.snake.boostMultiplier:2}")
    private int boostMultiplier;

    @Value("${soho.game.snake.foodMaxRatio:0.25}")
    private double foodMaxRatio;

    @Value("${soho.game.snake.pointsPerSegment:100}")
    private int pointsPerSegment;

    @Value("${soho.game.snake.battleDurationMillis:600000}")
    private long battleDurationMillis;

    @Value("${soho.game.snake.endlessRooms:10}")
    private int endlessRooms;

    @Value("${soho.game.snake.battleRooms:10}")
    private int battleRooms;

    @Value("${soho.game.snake.endlessMaxPlayers:100}")
    private int endlessMaxPlayers;

    @Value("${soho.game.snake.battleMaxPlayers:20}")
    private int battleMaxPlayers;

    @Value("${soho.game.snake.magnetDurationMillis:60000}")
    private long magnetDurationMillis;

    @Value("${soho.game.snake.reviveCardCost:500}")
    private int reviveCardCost;

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final GameSnakePlayerProfileService snakePlayerProfileService;
    private final Map<String, String> playerRoomIndex = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "snake-game-ticker");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * 启动全局游戏心跳。
     */
    @PostConstruct
    public void startTicker() {
        scheduler.scheduleAtFixedRate(this::tickAllRooms, TICK_MILLIS, TICK_MILLIS, TimeUnit.MILLISECONDS);
    }

    @PostConstruct
    public void initRooms() {
        for (int i = 1; i <= endlessRooms; i++) {
            createRoomIfAbsent("endless-" + i, GameRoomMode.ENDLESS, endlessMaxPlayers);
        }
        for (int i = 1; i <= battleRooms; i++) {
            createRoomIfAbsent("battle-" + i, GameRoomMode.BATTLE, battleMaxPlayers);
        }
    }

    /**
     * 停止心跳线程。
     */
    @PreDestroy
    public void stopTicker() {
        scheduler.shutdownNow();
    }

    /**
     * 处理长链接消息入口。
     */
    public void handleMessage(LongLinkMessage message, String connectId, String uid) {
        if (message == null) {
            return;
        }
        String topic = safeString(message.getTopic());
        String type = safeString(message.getType());
        Map<String, Object> payload = toMap(message.getPayload());
        String playerId = resolvePlayerId(uid, connectId);
        if (playerId == null) {
            return;
        }
        String roomId = resolveRoomId(payload, playerId);
        if ("room".equalsIgnoreCase(topic)) {
            if ("join".equalsIgnoreCase(type)) {
                handleJoin(roomId, payload, playerId, uid, connectId);
            } else if ("leave".equalsIgnoreCase(type)) {
                handleLeave(roomId, playerId);
            }
            return;
        }
        if ("ping".equalsIgnoreCase(topic) || "ping".equalsIgnoreCase(type)) {
            handlePing(payload, playerId, uid, connectId);
            return;
        }
        if ("round".equalsIgnoreCase(topic)) {
            if ("ready".equalsIgnoreCase(type)) {
                handleReady(roomId, playerId, payload);
            } else if ("start".equalsIgnoreCase(type)) {
                handleStart(roomId, playerId);
            } else if ("restart".equalsIgnoreCase(type)) {
                handleRestart(roomId, playerId);
            } else if ("input".equalsIgnoreCase(type)) {
                handleInput(roomId, playerId, payload);
            }
        }
    }

    /**
     * 加入房间，建立玩家与连接关系。
     */
    private void handleJoin(String roomId, Map<String, Object> payload, String playerId, String uid, String connectId) {
        String previousRoomId = playerRoomIndex.get(playerId);
        GameRoom previousRoom = null;
        if (previousRoomId != null && !previousRoomId.equals(roomId)) {
            previousRoom = rooms.get(previousRoomId);
        }

        GameRoomMode mode = parseMode(payload.get("mode"));
        GameRoom room = createRoomIfAbsent(roomId, mode, mode == GameRoomMode.BATTLE ? battleMaxPlayers : endlessMaxPlayers);
        synchronized (room.getLock()) {
            PlayerState player = room.getPlayers().get(playerId);
            boolean isNew = player == null;
            if (isNew && room.getPlayers().size() >= room.getMaxPlayers()) {
                PlayerState temp = new PlayerState();
                temp.setUid(uid);
                temp.setConnectId(connectId);
                sendRoomError(temp, "room is full");
                return;
            }
            if (player == null) {
                player = new PlayerState();
                player.setPlayerId(playerId);
                room.getPlayers().put(playerId, player);
            }
            if (room.getMode() == null) {
                room.setMode(mode);
            }
            if (room.getMode() == GameRoomMode.BATTLE && room.getOwnerPlayerId() == null) {
                room.setOwnerPlayerId(playerId);
            }
            player.setUid(uid);
            player.setConnectId(connectId);
            player.setName(safeString(payload.getOrDefault("name", playerId)));
            player.setReviveCards(loadReviveCards(playerId));
            room.setLastActiveAt(System.currentTimeMillis());
            playerRoomIndex.put(playerId, roomId);
        }
        if (previousRoom != null) {
            synchronized (previousRoom.getLock()) {
                removeFromRoom(previousRoom, playerId);
            }
            broadcastRoomSnapshot(previousRoom);
            sendRoundState(previousRoom);
        }
        if (room.getMode() == GameRoomMode.ENDLESS) {
            ensureEndlessRound(room);
            handleRestart(roomId, playerId);
        }
        broadcastRoomSnapshot(room);
        sendRoundState(room);
    }

    /**
     * 离开房间并清理索引。
     */
    private void handleLeave(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        synchronized (room.getLock()) {
            removeFromRoom(room, playerId);
        }
        broadcastRoomSnapshot(room);
        sendRoundState(room);
    }

    /**
     * 从指定房间移除玩家，必要时触发死亡逻辑。
     */
    private void removeFromRoom(GameRoom room, String playerId) {
        PlayerState player = room.getPlayers().remove(playerId);
        playerRoomIndex.remove(playerId);
        if (player != null && room.getRound() != null) {
            handlePlayerDeath(room.getRound(), playerId);
        }
    }

    /**
     * 更新玩家准备状态。
     */
    private void handleReady(String roomId, String playerId, Map<String, Object> payload) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        if (room.getMode() == GameRoomMode.ENDLESS) {
            return;
        }
        synchronized (room.getLock()) {
            PlayerState player = room.getPlayers().get(playerId);
            if (player == null) {
                return;
            }
            Object readyValue = payload.get("ready");
            boolean ready = true;
            if (readyValue instanceof Boolean) {
                ready = (Boolean) readyValue;
            } else if (readyValue != null) {
                ready = Boolean.parseBoolean(readyValue.toString());
            }
            player.setReady(ready);
        }
        broadcastRoomSnapshot(room);
    }

    /**
     * 创建新一局并初始化蛇与食物。
     */
    private void handleStart(String roomId) {
        handleStart(roomId, null);
    }

    private void handleStart(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        synchronized (room.getLock()) {
            if (room.getMode() == GameRoomMode.BATTLE && playerId != null
                    && room.getOwnerPlayerId() != null && !room.getOwnerPlayerId().equals(playerId)) {
                return;
            }
            if (room.getMode() == GameRoomMode.BATTLE && room.getPlayers().size() < 2) {
                return;
            }
            GameRound round = room.getRound();
            if (round != null && round.getStatus() == GameRound.Status.RUNNING) {
                return;
            }
            room.setRoundNo(room.getRoundNo() + 1);
            GameRound newRound = new GameRound();
            newRound.setRoundNo(room.getRoundNo());
            newRound.setWidth(DEFAULT_WIDTH);
            newRound.setHeight(DEFAULT_HEIGHT);
            newRound.setStatus(GameRound.Status.RUNNING);
            newRound.setStartedAt(System.currentTimeMillis());
            newRound.setDurationMillis(room.getMode() == GameRoomMode.BATTLE ? battleDurationMillis : 0L);
            initRound(room, newRound);
            room.setRound(newRound);
        }
        broadcastRoomSnapshot(room);
        sendRoundState(room);
    }

    /**
     * 允许新加入或已死亡玩家重新开始。
     */
    private void handleRestart(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return;
        }
        boolean startedNewRound = false;
        boolean createdPlayer = false;
        synchronized (room.getLock()) {
            if (!room.getPlayers().containsKey(playerId)) {
                PlayerState player = new PlayerState();
                player.setPlayerId(playerId);
                player.setName(playerId);
                room.getPlayers().put(playerId, player);
                createdPlayer = true;
            }
            GameRound round = room.getRound();
            if (round == null || round.getStatus() != GameRound.Status.RUNNING) {
                // 当前没有进行中的局，直接开新局
                room.setRoundNo(room.getRoundNo() + 1);
                GameRound newRound = new GameRound();
                newRound.setRoundNo(room.getRoundNo());
                newRound.setWidth(DEFAULT_WIDTH);
                newRound.setHeight(DEFAULT_HEIGHT);
                newRound.setStatus(GameRound.Status.RUNNING);
                newRound.setStartedAt(System.currentTimeMillis());
                newRound.setDurationMillis(room.getMode() == GameRoomMode.BATTLE ? battleDurationMillis : 0L);
                initRound(room, newRound);
                room.setRound(newRound);
                startedNewRound = true;
            }
            PlayerState player = room.getPlayers().get(playerId);
            if (player != null && player.isAlive() && !startedNewRound) {
                return;
            }
            if (!startedNewRound) {
                if (room.getMode() == GameRoomMode.ENDLESS && !createdPlayer && player != null && !player.isAlive()) {
                    if (parseUserId(player.getPlayerId()) == null) {
                        return;
                    }
                    if (player.getReviveCards() <= 0) {
                        return;
                    }
                    player.setReviveCards(player.getReviveCards() - 1);
                    persistReviveCards(player.getPlayerId(), player.getReviveCards());
                }
                int targetLength = player == null ? INITIAL_LENGTH : targetLengthForScore(player);
                SnakeState snake = createSafeSnake(round, targetLength);
                if (snake == null) {
                    return;
                }
                round.getSnakes().put(playerId, snake);
                if (player != null) {
                    if (createdPlayer) {
                        player.setReviveCards(loadReviveCards(playerId));
                        player.setScore(0);
                    }
                    player.setAlive(true);
                    player.setBoosting(false);
                    player.setLength(snake.getBody().size());
                    player.setMagnetActiveUntil(0L);
                }
            }
        }
        broadcastRoomSnapshot(room);
        sendRoundState(room);
    }

    /**
     * 网络时延测试，原样回传客户端时间戳。
     */
    private void handlePing(Map<String, Object> payload, String playerId, String uid, String connectId) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("clientTime", payload.get("clientTime"));
        resp.put("serverTime", System.currentTimeMillis());
        LongLinkMessage message = new LongLinkMessage();
        message.setNamespace("soho-game");
        message.setTopic("ping");
        message.setType("pong");
        message.setPayload(resp);
        String json = JacksonUtils.toJson(message);
        if (uid != null && !uid.isBlank()) {
            sender.sendToUid(uid, json);
        } else if (connectId != null && !connectId.isBlank()) {
            sender.sendToConnectId(connectId, json);
        } else if (playerId != null && !playerId.isBlank()) {
            sender.sendToUid(playerId, json);
        }
    }

    /**
     * 接收玩家方向输入。
     */
    private void handleInput(String roomId, String playerId, Map<String, Object> payload) {
        GameRoom room = rooms.get(roomId);
        if (room == null || room.getRound() == null) {
            return;
        }
        synchronized (room.getLock()) {
            SnakeState snake = room.getRound().getSnakes().get(playerId);
            if (snake == null) {
                return;
            }
            PlayerState player = room.getPlayers().get(playerId);
            if (player != null && payload.containsKey("boost")) {
                Object boostValue = payload.get("boost");
                boolean boosting = true;
                if (boostValue instanceof Boolean) {
                    boosting = (Boolean) boostValue;
                } else if (boostValue != null) {
                    boosting = Boolean.parseBoolean(boostValue.toString());
                }
                player.setBoosting(boosting);
            }
            String dirText = safeString(payload.get("direction"));
            if (dirText == null) {
                return;
            }
            Direction direction;
            try {
                direction = Direction.valueOf(dirText.toUpperCase());
            } catch (IllegalArgumentException e) {
                return;
            }
            if (!snake.getDirection().isOpposite(direction)) {
                snake.setNextDirection(direction);
            }
        }
    }

    /**
     * 初始化一局的蛇起点与食物。
     */
    private void initRound(GameRoom room, GameRound round) {
        List<PlayerState> players = new ArrayList<>(room.getPlayers().values());
        for (int i = 0; i < players.size(); i++) {
            PlayerState player = players.get(i);
            SnakeState snake = createSnake(i, players.size(), round.getWidth(), round.getHeight());
            round.getSnakes().put(player.getPlayerId(), snake);
            player.setAlive(true);
            player.setReady(false);
            player.setScore(0);
            player.setBoosting(false);
            player.setLength(INITIAL_LENGTH);
            player.setMagnetActiveUntil(0L);
        }
        spawnFoods(round, INITIAL_FOODS + players.size());
    }

    /**
     * 为玩家创建初始蛇身。
     */
    private SnakeState createSnake(int index, int total, int width, int height) {
        int x = width / 2;
        int y = height / 2;
        Direction direction = Direction.RIGHT;
        switch (index) {
            case 0:
                x = width / 4;
                y = height / 2;
                direction = Direction.RIGHT;
                break;
            case 1:
                x = width * 3 / 4;
                y = height / 2;
                direction = Direction.LEFT;
                break;
            case 2:
                x = width / 2;
                y = height / 4;
                direction = Direction.DOWN;
                break;
            case 3:
                x = width / 2;
                y = height * 3 / 4;
                direction = Direction.UP;
                break;
            case 4:
                x = width / 4;
                y = height / 4;
                direction = Direction.RIGHT;
                break;
            case 5:
                x = width * 3 / 4;
                y = height / 4;
                direction = Direction.LEFT;
                break;
            case 6:
                x = width / 4;
                y = height * 3 / 4;
                direction = Direction.RIGHT;
                break;
            case 7:
                x = width * 3 / 4;
                y = height * 3 / 4;
                direction = Direction.LEFT;
                break;
            default:
                x = ThreadLocalRandom.current().nextInt(2, width - 2);
                y = ThreadLocalRandom.current().nextInt(2, height - 2);
                direction = Direction.values()[index % Direction.values().length];
                break;
        }
        SnakeState snake = new SnakeState();
        snake.setDirection(direction);
        snake.setNextDirection(direction);
        for (int i = 0; i < INITIAL_LENGTH; i++) {
            int px = x - direction.getDx() * i;
            int py = y - direction.getDy() * i;
            snake.getBody().addLast(new Point(px, py));
        }
        return snake;
    }

    private SnakeState createSafeSnake(GameRound round) {
        return createSafeSnake(round, INITIAL_LENGTH);
    }

    private SnakeState createSafeSnake(GameRound round, int targetLength) {
        int length = Math.max(INITIAL_LENGTH, targetLength);
        Set<Point> occupied = new HashSet<>();
        for (FoodItem food : round.getFoods()) {
            occupied.add(new Point(food.getX(), food.getY()));
        }
        for (SnakeState snake : round.getSnakes().values()) {
            occupied.addAll(snake.getBody());
        }
        for (int i = 0; i < 50; i++) {
            Direction direction = Direction.values()[ThreadLocalRandom.current().nextInt(Direction.values().length)];
            int x = ThreadLocalRandom.current().nextInt(2, round.getWidth() - 2);
            int y = ThreadLocalRandom.current().nextInt(2, round.getHeight() - 2);
            boolean ok = true;
            Deque<Point> body = new ArrayDeque<>();
            for (int j = 0; j < length; j++) {
                int px = x - direction.getDx() * j;
                int py = y - direction.getDy() * j;
                Point point = new Point(px, py);
                if (px < 0 || px >= round.getWidth() || py < 0 || py >= round.getHeight()
                        || occupied.contains(point)) {
                    ok = false;
                    break;
                }
                body.addLast(point);
            }
            if (ok) {
                SnakeState snake = new SnakeState();
                snake.setDirection(direction);
                snake.setNextDirection(direction);
                snake.setBody(body);
                return snake;
            }
        }
        return null;
    }

    /**
     * 轮询所有房间。
     */
    private void tickAllRooms() {
        for (GameRoom room : rooms.values()) {
            tickRoom(room);
        }
    }

    /**
     * 推进单个房间一帧逻辑：移动、碰撞、吃食物与结算。
     */
    private void tickRoom(GameRoom room) {
        GameRound round = room.getRound();
        if (round == null || round.getStatus() != GameRound.Status.RUNNING) {
            return;
        }
        synchronized (room.getLock()) {
            if (round.getStatus() != GameRound.Status.RUNNING) {
                return;
            }
            if (room.getMode() == GameRoomMode.BATTLE && round.getDurationMillis() > 0) {
                long now = System.currentTimeMillis();
                if (round.getStartedAt() > 0 && now - round.getStartedAt() >= round.getDurationMillis()) {
                    round.setStatus(GameRound.Status.FINISHED);
                }
            }
            if (round.getStatus() == GameRound.Status.RUNNING) {
                round.setTick(round.getTick() + 1);
                Set<String> alivePlayers = new HashSet<>();
                Set<String> boostingPlayers = new HashSet<>();
                for (Map.Entry<String, PlayerState> entry : room.getPlayers().entrySet()) {
                    PlayerState player = entry.getValue();
                    if (player != null && player.isAlive()) {
                        alivePlayers.add(entry.getKey());
                        if (player.isBoosting()) {
                            boostingPlayers.add(entry.getKey());
                        }
                    }
                }
                stepMove(room, round, alivePlayers);
                int steps = Math.max(1, boostMultiplier);
                if (steps > 1 && !boostingPlayers.isEmpty() && round.getStatus() == GameRound.Status.RUNNING) {
                    for (int i = 1; i < steps; i++) {
                        stepMove(room, round, boostingPlayers);
                        if (round.getStatus() != GameRound.Status.RUNNING) {
                            break;
                        }
                    }
                }
                int aliveCount = 0;
                for (PlayerState player : room.getPlayers().values()) {
                    if (player.isAlive()) {
                        aliveCount++;
                    }
                }
                // 单人模式不自动结束，仅在多人参与时才允许结束本局。
                if (room.getMode() == GameRoomMode.BATTLE && aliveCount <= 1 && room.getPlayers().size() > 1) {
                    round.setStatus(GameRound.Status.FINISHED);
                }
            }
            if (round.getStatus() == GameRound.Status.FINISHED && !round.isResultSent()) {
                sendRoundResult(room);
                round.setResultSent(true);
            }
        }
        sendRoundState(room);
    }

    private void stepMove(GameRoom room, GameRound round, Set<String> movingPlayers) {
        Map<String, Point> nextHeads = new HashMap<>();
        Map<Point, String> occupiedOwner = new HashMap<>();
        Set<Point> sharedCells = new HashSet<>();

        for (Map.Entry<String, SnakeState> entry : round.getSnakes().entrySet()) {
            PlayerState player = room.getPlayers().get(entry.getKey());
            if (player == null || !player.isAlive()) {
                continue;
            }
            SnakeState snake = entry.getValue();
            if (snake.getBody().isEmpty()) {
                continue;
            }
            Set<Point> body = new HashSet<>(snake.getBody());
            for (Point point : body) {
                String owner = occupiedOwner.putIfAbsent(point, entry.getKey());
                if (owner != null && !owner.equals(entry.getKey())) {
                    sharedCells.add(point);
                }
            }
        }

        for (String playerId : movingPlayers) {
            PlayerState player = room.getPlayers().get(playerId);
            if (player == null || !player.isAlive()) {
                continue;
            }
            SnakeState snake = round.getSnakes().get(playerId);
            if (snake == null) {
                continue;
            }
            if (snake.getNextDirection() != null && !snake.getDirection().isOpposite(snake.getNextDirection())) {
                snake.setDirection(snake.getNextDirection());
            }
            Point head = snake.getBody().peekFirst();
            if (head == null) {
                continue;
            }
            Point next = new Point(head.getX() + snake.getDirection().getDx(),
                    head.getY() + snake.getDirection().getDy());
            nextHeads.put(playerId, next);
        }

        Set<String> deadPlayers = new HashSet<>();
        for (Map.Entry<String, Point> entry : nextHeads.entrySet()) {
            String playerId = entry.getKey();
            Point next = entry.getValue();
            if (next.getX() < 0 || next.getX() >= round.getWidth()
                    || next.getY() < 0 || next.getY() >= round.getHeight()) {
                deadPlayers.add(playerId);
                continue;
            }
            String owner = occupiedOwner.get(next);
            if (owner != null && (!owner.equals(playerId) || sharedCells.contains(next))) {
                deadPlayers.add(playerId);
            }
        }

        Map<Point, List<String>> headBuckets = new HashMap<>();
        for (Map.Entry<String, Point> entry : nextHeads.entrySet()) {
            String playerId = entry.getKey();
            if (deadPlayers.contains(playerId)) {
                continue;
            }
            headBuckets.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(playerId);
        }
        for (List<String> bucket : headBuckets.values()) {
            if (bucket.size() > 1) {
                deadPlayers.addAll(bucket);
            }
        }

        for (String playerId : movingPlayers) {
            PlayerState player = room.getPlayers().get(playerId);
            if (player == null) {
                continue;
            }
            SnakeState snake = round.getSnakes().get(playerId);
            if (snake == null || !player.isAlive()) {
                continue;
            }
            if (deadPlayers.contains(playerId)) {
                player.setLength(snake.getBody().size());
                player.setAlive(false);
                if (room.getMode() == GameRoomMode.ENDLESS) {
                    sendPlayerResult(room, player);
                    handlePlayerDeath(round, playerId);
                } else {
                    handlePlayerDeath(round, playerId);
                }
                continue;
            }
            Point next = nextHeads.get(playerId);
            if (next == null) {
                continue;
            }
            snake.getBody().addFirst(next);
            int maxFoods = maxFoodCount(round);
            boolean overMaxBeforeEat = round.getFoods().size() > maxFoods;
            long now = System.currentTimeMillis();
            int consumed = 0;
            FoodItem food = consumeFoodAt(round, next);
            if (food != null) {
                applyFoodEffect(player, food, now);
                consumed++;
            }
            if (hasActiveMagnet(player, now)) {
                consumed += absorbAdjacentFoods(round, player, next, now);
            }
            if (consumed > 0 && !overMaxBeforeEat) {
                spawnFoods(round, consumed);
            }
            trimSnakeToScore(player, snake);
        }
    }

    /**
     * 玩家死亡后将蛇身转为食物。
     */
    private void handlePlayerDeath(GameRound round, String playerId) {
        SnakeState snake = round.getSnakes().get(playerId);
        if (snake == null) {
            return;
        }
        for (Point point : snake.getBody()) {
            round.getFoods().add(createFood(point));
        }
        snake.getBody().clear();
        limitFoods(round);
    }

    /**
     * 在空位随机生成食物。
     */
    private void spawnFoods(GameRound round, int count) {
        int maxFoods = maxFoodCount(round);
        if (round.getFoods().size() >= maxFoods) {
            return;
        }
        Set<Point> occupied = new HashSet<>();
        for (SnakeState snake : round.getSnakes().values()) {
            occupied.addAll(snake.getBody());
        }
        int attempts = 0;
        int target = Math.min(round.getFoods().size() + count, maxFoods);
        int remaining = target - round.getFoods().size();
        while (round.getFoods().size() < target && attempts < remaining * 50) {
            int x = ThreadLocalRandom.current().nextInt(0, round.getWidth());
            int y = ThreadLocalRandom.current().nextInt(0, round.getHeight());
            Point point = new Point(x, y);
            if (occupied.contains(point) || findFoodAt(round, point) != null) {
                attempts++;
                continue;
            }
            round.getFoods().add(createFood(point));
        }
    }

    private int maxFoodCount(GameRound round) {
        int cells = round.getWidth() * round.getHeight();
        double ratio = Math.min(1.0, Math.max(0.0, foodMaxRatio));
        return Math.max(1, (int) Math.floor(cells * ratio));
    }

    private void limitFoods(GameRound round) {
        int maxFoods = maxFoodCount(round);
        if (round.getFoods().size() <= maxFoods) {
            return;
        }
        int removeCount = round.getFoods().size() - maxFoods;
        int removed = 0;
        var iterator = round.getFoods().iterator();
        while (iterator.hasNext() && removed < removeCount) {
            iterator.next();
            iterator.remove();
            removed++;
        }
    }

    /**
     * 广播房间快照。
     */
    private void broadcastRoomSnapshot(GameRoom room) {
        RoomSnapshot snapshot = buildRoomSnapshot(room);
        LongLinkMessage message = new LongLinkMessage();
        message.setNamespace("soho-game");
        message.setTopic("room");
        message.setType("snapshot");
        message.setPayload(snapshot);
        sendToRoom(room, message);
    }

    /**
     * 广播局状态。
     */
    private void sendRoundState(GameRoom room) {
        GameRound round = room.getRound();
        if (round == null) {
            return;
        }
        RoundState state = buildRoundState(room);
        LongLinkMessage message = new LongLinkMessage();
        message.setNamespace("soho-game");
        message.setTopic("round");
        message.setType("state");
        message.setPayload(state);
        sendToRoom(room, message);
    }

    private RoomSnapshot buildRoomSnapshot(GameRoom room) {
        RoomSnapshot snapshot = new RoomSnapshot();
        snapshot.setRoomId(room.getRoomId());
        snapshot.setMode(room.getMode());
        snapshot.setRoundNo(room.getRoundNo());
        snapshot.setStatus(room.getRound() == null ? GameRound.Status.WAITING : room.getRound().getStatus());
        List<PlayerView> players = new ArrayList<>();
        for (PlayerState player : room.getPlayers().values()) {
            PlayerView view = new PlayerView();
            view.setPlayerId(player.getPlayerId());
            view.setName(player.getName());
            view.setReady(player.isReady());
            view.setAlive(player.isAlive());
            view.setScore(player.getScore());
            view.setLength(player.getLength());
            view.setReviveCards(player.getReviveCards());
            players.add(view);
        }
        snapshot.setPlayers(players);
        return snapshot;
    }

    private RoundState buildRoundState(GameRoom room) {
        GameRound round = room.getRound();
        RoundState state = new RoundState();
        state.setRoomId(room.getRoomId());
        state.setRoundNo(round.getRoundNo());
        state.setTick(round.getTick());
        state.setWidth(round.getWidth());
        state.setHeight(round.getHeight());
        state.setStatus(round.getStatus());
        state.setFoods(new ArrayList<>(round.getFoods()));
        List<SnakeView> snakes = new ArrayList<>();
        for (Map.Entry<String, SnakeState> entry : round.getSnakes().entrySet()) {
            SnakeView view = new SnakeView();
            view.setPlayerId(entry.getKey());
            PlayerState player = room.getPlayers().get(entry.getKey());
            view.setAlive(player != null && player.isAlive());
            view.setBoosting(player != null && player.isBoosting());
            view.setMagnetUntil(player == null ? null : player.getMagnetActiveUntil());
            view.setBody(new ArrayList<>(entry.getValue().getBody()));
            snakes.add(view);
        }
        state.setSnakes(snakes);
        return state;
    }

    private void sendRoundResult(GameRoom room) {
        if (room.getMode() != GameRoomMode.BATTLE) {
            return;
        }
        GameRound round = room.getRound();
        if (round == null) {
            return;
        }
        RoundResult result = new RoundResult();
        result.setRoomId(room.getRoomId());
        result.setRoundNo(round.getRoundNo());
        result.setMode(room.getMode());
        List<RoundResult.ResultItem> rankings = new ArrayList<>();
        for (PlayerState player : room.getPlayers().values()) {
            RoundResult.ResultItem item = new RoundResult.ResultItem();
            item.setPlayerId(player.getPlayerId());
            item.setName(player.getName());
            item.setScore(player.getScore());
            item.setLength(player.getLength());
            item.setAlive(player.isAlive());
            rankings.add(item);
        }
        rankings.sort((a, b) -> {
            int cmp = Integer.compare(b.getLength(), a.getLength());
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(b.getScore(), a.getScore());
        });
        int rank = 1;
        for (RoundResult.ResultItem item : rankings) {
            item.setRank(rank++);
        }
        result.setRankings(rankings);
        LongLinkMessage message = new LongLinkMessage();
        message.setNamespace("soho-game");
        message.setTopic("round");
        message.setType("result");
        message.setPayload(result);
        sendToRoom(room, message);
    }

    private void sendPlayerResult(GameRoom room, PlayerState player) {
        if (player == null) {
            return;
        }
        GameRound round = room.getRound();
        RoundResult result = new RoundResult();
        result.setRoomId(room.getRoomId());
        result.setRoundNo(round == null ? room.getRoundNo() : round.getRoundNo());
        result.setMode(room.getMode());
        RoundResult.ResultItem item = new RoundResult.ResultItem();
        item.setPlayerId(player.getPlayerId());
        item.setName(player.getName());
        item.setScore(player.getScore());
        item.setLength(player.getLength());
        item.setAlive(player.isAlive());
        item.setRank(1);
        result.setRankings(List.of(item));
        LongLinkMessage message = new LongLinkMessage();
        message.setNamespace("soho-game");
        message.setTopic("round");
        message.setType("result");
        message.setPayload(result);
        String json = JacksonUtils.toJson(message);
        if (player.getUid() != null && !player.getUid().isBlank()) {
            sender.sendToUid(player.getUid(), json);
        } else if (player.getConnectId() != null && !player.getConnectId().isBlank()) {
            sender.sendToConnectId(player.getConnectId(), json);
        }
    }

    public boolean revivePlayer(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }
        if (parseUserId(playerId) == null) {
            return false;
        }
        boolean revived = false;
        synchronized (room.getLock()) {
            if (room.getMode() != GameRoomMode.ENDLESS) {
                return false;
            }
            if (room.getRound() == null || room.getRound().getStatus() != GameRound.Status.RUNNING) {
                ensureEndlessRound(room);
            }
            GameRound round = room.getRound();
            if (round == null || round.getStatus() != GameRound.Status.RUNNING) {
                return false;
            }
            PlayerState player = room.getPlayers().get(playerId);
            if (player == null || player.isAlive()) {
                return false;
            }
            if (player.getReviveCards() <= 0) {
                return false;
            }
            int targetLength = targetLengthForScore(player);
            SnakeState snake = createSafeSnake(round, targetLength);
            if (snake == null) {
                return false;
            }
            round.getSnakes().put(playerId, snake);
            player.setReviveCards(player.getReviveCards() - 1);
            player.setAlive(true);
            player.setBoosting(false);
            player.setLength(snake.getBody().size());
            player.setMagnetActiveUntil(0L);
            persistReviveCards(player.getPlayerId(), player.getReviveCards());
            revived = true;
        }
        if (revived) {
            broadcastRoomSnapshot(room);
            sendRoundState(room);
        }
        return revived;
    }

    public Integer exchangeReviveCards(String roomId, String playerId, int count) {
        if (count <= 0) {
            return null;
        }
        if (reviveCardCost <= 0) {
            return null;
        }
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        if (parseUserId(playerId) == null) {
            return null;
        }
        Integer cards = null;
        synchronized (room.getLock()) {
            if (room.getMode() != GameRoomMode.ENDLESS) {
                return null;
            }
            PlayerState player = room.getPlayers().get(playerId);
            if (player == null) {
                return null;
            }
            long cost = (long) reviveCardCost * count;
            if (player.getScore() < cost) {
                return null;
            }
            player.setScore((int) (player.getScore() - cost));
            player.setReviveCards(player.getReviveCards() + count);
            persistReviveCards(player.getPlayerId(), player.getReviveCards());
            GameRound round = room.getRound();
            if (round != null) {
                SnakeState snake = round.getSnakes().get(playerId);
                if (snake != null) {
                    trimSnakeToScore(player, snake);
                }
            }
            cards = player.getReviveCards();
        }
        if (cards != null) {
            broadcastRoomSnapshot(room);
            sendRoundState(room);
        }
        return cards;
    }

    public Integer grantReviveCards(String roomId, String playerId, int count) {
        if (count <= 0) {
            return null;
        }
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        Integer cards = null;
        synchronized (room.getLock()) {
            PlayerState player = room.getPlayers().get(playerId);
            if (player == null) {
                return null;
            }
            player.setReviveCards(player.getReviveCards() + count);
            persistReviveCards(player.getPlayerId(), player.getReviveCards());
            cards = player.getReviveCards();
        }
        if (cards != null) {
            broadcastRoomSnapshot(room);
            sendRoundState(room);
        }
        return cards;
    }

    public List<work.soho.game.biz.snake.dto.AdminRoomSummary> listRoomSummaries() {
        List<work.soho.game.biz.snake.dto.AdminRoomSummary> summaries = new ArrayList<>();
        for (GameRoom room : rooms.values()) {
            synchronized (room.getLock()) {
                work.soho.game.biz.snake.dto.AdminRoomSummary summary =
                        new work.soho.game.biz.snake.dto.AdminRoomSummary();
                summary.setRoomId(room.getRoomId());
                summary.setMode(room.getMode());
                summary.setRoundNo(room.getRoundNo());
                summary.setStatus(room.getRound() == null ? GameRound.Status.WAITING : room.getRound().getStatus());
                summary.setPlayerCount(room.getPlayers().size());
                int aliveCount = 0;
                for (PlayerState player : room.getPlayers().values()) {
                    if (player.isAlive()) {
                        aliveCount++;
                    }
                }
                summary.setAliveCount(aliveCount);
                summary.setLastActiveAt(room.getLastActiveAt());
                summaries.add(summary);
            }
        }
        return summaries;
    }

    public work.soho.game.biz.snake.dto.AdminRoomDetail getRoomDetail(String roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        synchronized (room.getLock()) {
            work.soho.game.biz.snake.dto.AdminRoomDetail detail =
                    new work.soho.game.biz.snake.dto.AdminRoomDetail();
            detail.setSnapshot(buildRoomSnapshot(room));
            detail.setRound(room.getRound() == null ? null : buildRoundState(room));
            return detail;
        }
    }

    public boolean forceFinish(String roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null || room.getRound() == null) {
            return false;
        }
        synchronized (room.getLock()) {
            if (room.getRound() == null) {
                return false;
            }
            room.getRound().setStatus(GameRound.Status.FINISHED);
            if (!room.getRound().isResultSent()) {
                sendRoundResult(room);
                room.getRound().setResultSent(true);
            }
        }
        broadcastRoomSnapshot(room);
        sendRoundState(room);
        return true;
    }

    public boolean kickPlayer(String roomId, String playerId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }
        synchronized (room.getLock()) {
            if (!room.getPlayers().containsKey(playerId)) {
                return false;
            }
            removeFromRoom(room, playerId);
        }
        broadcastRoomSnapshot(room);
        sendRoundState(room);
        return true;
    }

    public work.soho.game.biz.snake.dto.AdminSnakeConfig getConfig() {
        work.soho.game.biz.snake.dto.AdminSnakeConfig config =
                new work.soho.game.biz.snake.dto.AdminSnakeConfig();
        config.setBoostMultiplier(boostMultiplier);
        config.setFoodMaxRatio(foodMaxRatio);
        return config;
    }

    public boolean updateConfig(Integer boostMultiplier, Double foodMaxRatio) {
        boolean updated = false;
        if (boostMultiplier != null && boostMultiplier >= 1) {
            this.boostMultiplier = boostMultiplier;
            updated = true;
        }
        if (foodMaxRatio != null && foodMaxRatio > 0 && foodMaxRatio <= 1) {
            this.foodMaxRatio = foodMaxRatio;
            updated = true;
        }
        if (updated) {
            for (GameRoom room : rooms.values()) {
                synchronized (room.getLock()) {
                    if (room.getRound() != null) {
                        limitFoods(room.getRound());
                    }
                }
            }
        }
        return updated;
    }

    public RoomSnapshot autoJoin(GameRoomMode mode, String playerId, String uid, String name) {
        if (playerId == null || playerId.isBlank()) {
            return null;
        }
        GameRoomMode targetMode = mode == null ? GameRoomMode.ENDLESS : mode;
        GameRoom room = findAvailableRoom(targetMode);
        if (room == null) {
            room = createRoomIfAbsent(
                    targetMode.name().toLowerCase() + "-" + System.currentTimeMillis(),
                    targetMode,
                    targetMode == GameRoomMode.BATTLE ? battleMaxPlayers : endlessMaxPlayers);
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("roomId", room.getRoomId());
        payload.put("name", name == null ? playerId : name);
        payload.put("mode", targetMode.name());
        handleJoin(room.getRoomId(), payload, playerId, uid, null);
        synchronized (room.getLock()) {
            if (!room.getPlayers().containsKey(playerId)) {
                return null;
            }
            return buildRoomSnapshot(room);
        }
    }

    private GameRoom findAvailableRoom(GameRoomMode mode) {
        GameRoom selected = null;
        int minPlayers = Integer.MAX_VALUE;
        for (GameRoom room : rooms.values()) {
            if (room.getMode() != mode) {
                continue;
            }
            synchronized (room.getLock()) {
                if (room.getPlayers().size() >= room.getMaxPlayers()) {
                    continue;
                }
                if (mode == GameRoomMode.BATTLE) {
                    if (room.getRound() != null && room.getRound().getStatus() == GameRound.Status.RUNNING) {
                        continue;
                    }
                }
                if (room.getPlayers().size() < minPlayers) {
                    minPlayers = room.getPlayers().size();
                    selected = room;
                }
            }
        }
        return selected;
    }

    /**
     * 向房间所有玩家发送消息。
     */
    private void sendToRoom(GameRoom room, LongLinkMessage message) {
        if (room.getPlayers().isEmpty()) {
            return;
        }
        String json = JacksonUtils.toJson(message);
        for (PlayerState player : room.getPlayers().values()) {
            if (player.getUid() != null && !player.getUid().isBlank()) {
                sender.sendToUid(player.getUid(), json);
            } else if (player.getConnectId() != null && !player.getConnectId().isBlank()) {
                sender.sendToConnectId(player.getConnectId(), json);
            }
        }
    }

    private String resolvePlayerId(String uid, String connectId) {
        if (uid != null && !uid.isBlank()) {
            return uid;
        }
        if (connectId != null && !connectId.isBlank()) {
            return connectId;
        }
        return null;
    }

    private String resolveRoomId(Map<String, Object> payload, String playerId) {
        Object roomIdValue = payload.get("roomId");
        if (roomIdValue != null) {
            String roomId = roomIdValue.toString();
            if (!roomId.isBlank()) {
                return roomId;
            }
        }
        String indexed = playerRoomIndex.get(playerId);
        if (indexed != null && !indexed.isBlank()) {
            return indexed;
        }
        return "default";
    }

    private void ensureEndlessRound(GameRoom room) {
        synchronized (room.getLock()) {
            if (room.getRound() != null && room.getRound().getStatus() == GameRound.Status.RUNNING) {
                return;
            }
            room.setRoundNo(room.getRoundNo() + 1);
            GameRound round = new GameRound();
            round.setRoundNo(room.getRoundNo());
            round.setWidth(DEFAULT_WIDTH);
            round.setHeight(DEFAULT_HEIGHT);
            round.setStatus(GameRound.Status.RUNNING);
            round.setStartedAt(System.currentTimeMillis());
            initRound(room, round);
            room.setRound(round);
        }
    }

    private void trimSnakeToScore(PlayerState player, SnakeState snake) {
        int targetLength = targetLengthForScore(player);
        while (snake.getBody().size() > targetLength) {
            snake.getBody().pollLast();
        }
        player.setLength(snake.getBody().size());
    }

    private int targetLengthForScore(PlayerState player) {
        int targetLength = INITIAL_LENGTH;
        if (player == null) {
            return targetLength;
        }
        if (pointsPerSegment > 0) {
            targetLength += player.getScore() / pointsPerSegment;
        } else {
            targetLength += player.getScore() / DEFAULT_POINTS_PER_SEGMENT;
        }
        return Math.max(INITIAL_LENGTH, targetLength);
    }

    private FoodItem createFood(Point point) {
        FoodType[] types = FoodType.values();
        FoodType type = types[ThreadLocalRandom.current().nextInt(types.length)];
        return new FoodItem(point.getX(), point.getY(), type, type.getScore());
    }

    private FoodItem consumeFoodAt(GameRound round, Point point) {
        FoodItem food = findFoodAt(round, point);
        if (food != null) {
            round.getFoods().remove(food);
        }
        return food;
    }

    private void applyFoodEffect(PlayerState player, FoodItem food, long now) {
        if (player == null || food == null) {
            return;
        }
        player.setScore(player.getScore() + food.getScore());
        if (food.getType() == FoodType.MAGNET) {
            player.setMagnetActiveUntil(now + magnetDurationMillis);
        }
    }

    private boolean hasActiveMagnet(PlayerState player, long now) {
        return player != null && player.getMagnetActiveUntil() > now;
    }

    private int absorbAdjacentFoods(GameRound round, PlayerState player, Point head, long now) {
        int absorbed = 0;
        for (Direction direction : Direction.values()) {
            Point point = new Point(head.getX() + direction.getDx(), head.getY() + direction.getDy());
            FoodItem food = consumeFoodAt(round, point);
            if (food != null) {
                applyFoodEffect(player, food, now);
                absorbed++;
            }
        }
        return absorbed;
    }

    private FoodItem findFoodAt(GameRound round, Point point) {
        for (FoodItem food : round.getFoods()) {
            if (food.getX() == point.getX() && food.getY() == point.getY()) {
                return food;
            }
        }
        return null;
    }

    private int loadReviveCards(String playerId) {
        Long userId = parseUserId(playerId);
        if (userId == null) {
            return 0;
        }
        GameSnakePlayerProfile profile = snakePlayerProfileService.getOne(
                new LambdaQueryWrapper<GameSnakePlayerProfile>().eq(GameSnakePlayerProfile::getUserId, userId));
        if (profile == null) {
            profile = new GameSnakePlayerProfile();
            profile.setUserId(userId);
            profile.setReviveCards(0);
            snakePlayerProfileService.save(profile);
            return 0;
        }
        Integer cards = profile.getReviveCards();
        return cards == null ? 0 : cards;
    }

    private void persistReviveCards(String playerId, int reviveCards) {
        Long userId = parseUserId(playerId);
        if (userId == null) {
            return;
        }
        GameSnakePlayerProfile profile = snakePlayerProfileService.getOne(
                new LambdaQueryWrapper<GameSnakePlayerProfile>().eq(GameSnakePlayerProfile::getUserId, userId));
        if (profile == null) {
            profile = new GameSnakePlayerProfile();
            profile.setUserId(userId);
            profile.setReviveCards(reviveCards);
            snakePlayerProfileService.save(profile);
            return;
        }
        profile.setReviveCards(reviveCards);
        snakePlayerProfileService.updateById(profile);
    }

    private Long parseUserId(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(playerId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private GameRoomMode parseMode(Object value) {
        if (value == null) {
            return GameRoomMode.ENDLESS;
        }
        String text = value.toString().trim().toUpperCase();
        if (text.isEmpty()) {
            return GameRoomMode.ENDLESS;
        }
        try {
            return GameRoomMode.valueOf(text);
        } catch (IllegalArgumentException e) {
            return GameRoomMode.ENDLESS;
        }
    }

    private GameRoom createRoomIfAbsent(String roomId, GameRoomMode mode, int maxPlayers) {
        return rooms.computeIfAbsent(roomId, id -> {
            GameRoom created = new GameRoom();
            created.setRoomId(id);
            created.setMode(mode == null ? GameRoomMode.ENDLESS : mode);
            created.setMaxPlayers(maxPlayers);
            created.setCreatedAt(System.currentTimeMillis());
            created.setLastActiveAt(System.currentTimeMillis());
            return created;
        });
    }

    private void sendRoomError(PlayerState player, String message) {
        if (player == null) {
            return;
        }
        LongLinkMessage error = new LongLinkMessage();
        error.setNamespace("soho-game");
        error.setTopic("room");
        error.setType("error");
        error.setPayload(message);
        String json = JacksonUtils.toJson(error);
        if (player.getUid() != null && !player.getUid().isBlank()) {
            sender.sendToUid(player.getUid(), json);
        } else if (player.getConnectId() != null && !player.getConnectId().isBlank()) {
            sender.sendToConnectId(player.getConnectId(), json);
        }
    }

    private Map<String, Object> toMap(Object payload) {
        if (payload instanceof Map) {
            return (Map<String, Object>) payload;
        }
        if (payload instanceof String) {
            Map<String, Object> map = JacksonUtils.toBean((String) payload, Map.class);
            if (map != null) {
                return map;
            }
        }
        return new HashMap<>();
    }

    private String safeString(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }
}
