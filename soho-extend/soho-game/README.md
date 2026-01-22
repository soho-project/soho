# soho-game

## 模块说明
soho-game 提供多人贪吃蛇服务端能力，基于 soho-long-link 进行实时消息通信，支持房间与局次概念。

## 接入方式
- WebSocket 地址: `ws://<host>:<port>/ws`
- 认证方式沿用 long-link (header/cookie/query/首帧 token)

## 消息协议
统一使用 LongLinkMessage:

```json
{
  "namespace": "soho-game",
  "topic": "room|round",
  "type": "join|leave|ready|start|restart|input|snapshot|state|result|ping|pong",
  "payload": {}
}
```

## 客户端发送示例
### 加入房间
```json
{
  "namespace": "soho-game",
  "topic": "room",
  "type": "join",
  "payload": { "roomId": "r1", "name": "Alice", "mode": "ENDLESS|BATTLE" }
}
```

### 开始新局
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "start",
  "payload": { "roomId": "r1" }
}
```

### 重新开始
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "restart",
  "payload": { "roomId": "r1" }
}
```

### 移动方向/加速
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "input",
  "payload": { "roomId": "r1", "direction": "UP", "boost": true }
}
```

direction 可选: `UP|DOWN|LEFT|RIGHT`

### 时延测试
```json
{
  "namespace": "soho-game",
  "topic": "ping",
  "type": "ping",
  "payload": { "clientTime": 1730000000000 }
}
```

## 服务端广播示例
### 房间快照 (room/snapshot)
```json
{
  "namespace": "soho-game",
  "topic": "room",
  "type": "snapshot",
  "payload": {
    "roomId": "r1",
    "mode": "ENDLESS",
    "roundNo": 1,
    "status": "WAITING|RUNNING|FINISHED",
    "players": [
      { "playerId": "uid1", "name": "Alice", "ready": true, "alive": true, "score": 200, "length": 5, "reviveCards": 1 }
    ]
  }
}
```

### 局状态 (round/state)
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "state",
  "payload": {
    "roomId": "r1",
    "roundNo": 1,
    "tick": 42,
    "width": 40,
    "height": 30,
    "status": "RUNNING",
    "foods": [ { "x": 3, "y": 8, "type": "APPLE", "score": 50 } ],
    "snakes": [
      { "playerId": "uid1", "alive": true, "boosting": true, "magnetUntil": 0, "body": [ { "x": 5, "y": 6 } ] }
    ]
  }
}
```

### 结算通知 (round/result)
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "result",
  "payload": {
    "roomId": "battle-1",
    "roundNo": 1,
    "mode": "BATTLE",
    "rankings": [
      { "playerId": "uid1", "name": "Alice", "score": 500, "length": 8, "alive": true, "rank": 1 },
      { "playerId": "uid2", "name": "Bob", "score": 350, "length": 6, "alive": false, "rank": 2 }
    ]
  }
}
```

### 时延测试响应 (ping/pong)
```json
{
  "namespace": "soho-game",
  "topic": "ping",
  "type": "pong",
  "payload": { "clientTime": 1730000000000, "serverTime": 1730000000100 }
}
```

## 规则说明
- 食物类型: APPLE/BANANA/PIZZA/GRAPE/MAGNET，其中 MAGNET 积分为 0
- 吃到 MAGNET 后，在持续时间内可以吸收上下左右相邻格子的食物
- 积分决定蛇长度: 每 100 积分增加 1 节 (可配置)
- `start` 创建新一局并重置蛇与食物
- `restart` 允许新加入或已死亡玩家重新生成蛇
- 蛇死亡会把身体坐标转成食物
- 可通过 `soho.game.snake.boostMultiplier` 配置加速倍数 (默认 2)
- 可通过 `soho.game.snake.foodMaxRatio` 配置食物占比上限 (默认 0.25)
- 可通过 `soho.game.snake.magnetDurationMillis` 配置磁铁持续时间 (默认 60000)
- 可通过 `soho.game.snake.reviveCardCost` 配置复活卡兑换积分成本 (默认 500)
- 撞墙/撞其它蛇/同时撞头判定死亡
- 对战模式下存活数 <= 1 时结束
- 无尽模式下死亡会向个人下发结算消息 (round/result)，可使用复活卡复活
- 无尽模式复活会保留积分与蛇长度
- 无尽模式未选择复活时，再次加入会重置积分并正常开局

## 后台管理 API
- `GET /game/admin/snake/rooms` 房间列表
- `GET /game/admin/snake/rooms/{roomId}` 房间详情
- `POST /game/admin/snake/rooms/{roomId}/finish` 强制结束局
- `POST /game/admin/snake/rooms/{roomId}/kick/{playerId}` 踢出玩家
- `GET /game/admin/snake/config` 获取配置
- `PUT /game/admin/snake/config` 更新配置
- `POST /game/admin/snake/reviveCards/grant` 发放复活卡

## 自动进入房间 API
- `GET /game/guest/snake/autoJoin?mode=ENDLESS|BATTLE&name=xxx&playerId=xxx`

## 复活与复活卡 API
- `POST /game/guest/snake/revive` (需登录)
- `POST /game/guest/snake/reviveCard/exchange` (需登录)
