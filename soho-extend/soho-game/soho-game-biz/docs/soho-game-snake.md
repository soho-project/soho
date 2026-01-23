# soho-game 多人贪吃蛇 对接文档

## 1. 连接方式
- 使用长链接模块 WebSocket: `ws://<host>:<port>/ws`
- 认证方式沿用 long-link (header/cookie/query/首帧 token)

## 2. 消息格式
统一使用 LongLinkMessage:

```json
{
  "namespace": "soho-game",
  "topic": "room|round",
  "type": "join|leave|ready|start|restart|input|snapshot|state|result|ping|pong",
  "payload": {}
}
```

## 3. 客户端 -> 服务端
### 3.1 加入房间
```json
{
  "namespace": "soho-game",
  "topic": "room",
  "type": "join",
  "payload": { "roomId": "r1", "name": "Alice", "mode": "ENDLESS|BATTLE" }
}
```

### 3.2 离开房间
```json
{
  "namespace": "soho-game",
  "topic": "room",
  "type": "leave",
  "payload": { "roomId": "r1" }
}
```

### 3.3 准备/取消准备
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "ready",
  "payload": { "roomId": "r1", "ready": true }
}
```

### 3.4 开始新局
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "start",
  "payload": { "roomId": "r1" }
}
```

### 3.5 重新开始
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "restart",
  "payload": { "roomId": "r1" }
}
```

### 3.6 移动方向/加速
```json
{
  "namespace": "soho-game",
  "topic": "round",
  "type": "input",
  "payload": { "roomId": "r1", "direction": "UP", "boost": true }
}
```

direction 可选: `UP|DOWN|LEFT|RIGHT`

### 3.7 时延测试
```json
{
  "namespace": "soho-game",
  "topic": "ping",
  "type": "ping",
  "payload": { "clientTime": 1730000000000 }
}
```

## 4. 服务端 -> 客户端
### 4.1 房间快照 (room/snapshot)
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
      { "playerId": "uid1", "name": "Alice", "ready": true, "alive": true, "score": 200, "length": 5, "reviveCards": 1 },
      { "playerId": "uid2", "name": "Bob", "ready": false, "alive": true, "score": 100, "length": 4, "reviveCards": 0 }
    ]
  }
}
```

### 4.2 局状态 (round/state)
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
    "foods": [ { "x": 3, "y": 8, "type": "APPLE", "score": 50 }, { "x": 12, "y": 9, "type": "PIZZA", "score": 150 } ],
    "snakes": [
      { "playerId": "uid1", "alive": true, "boosting": true, "magnetUntil": 1730000060000, "body": [ { "x": 5, "y": 6 }, { "x": 4, "y": 6 } ] },
      { "playerId": "uid2", "alive": false, "boosting": false, "magnetUntil": 0, "body": [] }
    ]
  }
}
```

### 4.3 结算通知 (round/result)
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

### 4.4 时延测试响应 (ping/pong)
```json
{
  "namespace": "soho-game",
  "topic": "ping",
  "type": "pong",
  "payload": { "clientTime": 1730000000000, "serverTime": 1730000000100 }
}
```

## 5. 规则说明
- 食物类型: APPLE/BANANA/PIZZA/GRAPE/MAGNET，其中 MAGNET 积分为 0
- 吃到 MAGNET 后，在持续时间内可以吸收上下左右相邻格子的食物
- 积分决定蛇长度: 每 100 积分增加 1 节 (可配置)
- `start` 创建新一局并重置蛇
- `restart` 允许新加入或已死亡玩家重新生成蛇
- 蛇死亡会把身体坐标转成食物
- 撞墙/撞其它蛇/同时撞头判定死亡
- 对战模式下存活数 <= 1 时结束 (status=FINISHED)
- 无尽模式下死亡会向个人下发结算消息 (round/result)，可使用复活卡复活
- 无尽模式未选择复活时，再次加入会重置积分并正常开局

## 7. 加速配置
- 属性: `soho.game.snake.boostMultiplier`
- 说明: 加速倍数，默认 2
- 属性: `soho.game.snake.foodMaxRatio`
- 说明: 食物占比上限，默认 0.25
- 属性: `soho.game.snake.pointsPerSegment`
- 说明: 每节所需积分，默认 100
- 属性: `soho.game.snake.battleDurationMillis`
- 说明: 对战模式单局时长，默认 600000 (10分钟)
- 属性: `soho.game.snake.magnetDurationMillis`
- 说明: 磁铁持续时间，默认 60000 (1分钟)
- 属性: `soho.game.snake.reviveCardCost`
- 说明: 复活卡兑换积分成本，默认 500
- 属性: `soho.game.snake.endlessMaxPlayers`
- 说明: 无尽模式房间人数上限，默认 100
- 属性: `soho.game.snake.battleMaxPlayers`
- 说明: 对战模式房间人数上限，默认 20
- 属性: `soho.game.snake.endlessRooms`
- 说明: 默认无尽模式房间数量，默认 10
- 属性: `soho.game.snake.battleRooms`
- 说明: 默认对战模式房间数量，默认 10

## 6. 注意事项
- `roomId` 为空时会默认加入 `default` 房间
- 未登录时服务端会用 `connectId` 作为玩家 ID
- tick 默认 200ms 广播一次
- 无尽模式: 进入即开始，死亡后退出
- 对战模式: 进入后准备，房主开始开局，单局 10 分钟按最终长度排名

## 8. 后台管理 API
### 8.1 房间列表
- `GET /game/admin/snake/rooms`
- 请求参数: 无
- 返回:
```json
{
  "code": 0,
  "data": [
    {
      "roomId": "default",
      "mode": "ENDLESS",
      "roundNo": 1,
      "status": "RUNNING",
      "playerCount": 3,
      "aliveCount": 2,
      "lastActiveAt": 1730000000000
    }
  ],
  "msg": "success"
}
```

### 8.2 房间详情
- `GET /game/admin/snake/rooms/{roomId}`
- 路径参数: `roomId`
- 返回:
```json
{
  "code": 0,
  "data": {
    "snapshot": {
      "roomId": "default",
      "mode": "ENDLESS",
      "roundNo": 1,
      "status": "RUNNING",
      "players": [
        { "playerId": "uid1", "name": "Alice", "ready": true, "alive": true, "score": 200, "length": 5, "reviveCards": 1 }
      ]
    },
    "round": {
      "roomId": "default",
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
  },
  "msg": "success"
}
```

### 8.3 强制结束局
- `POST /game/admin/snake/rooms/{roomId}/finish`
- 路径参数: `roomId`
- 返回:
```json
{ "code": 0, "data": true, "msg": "success" }
```

### 8.4 踢出玩家
- `POST /game/admin/snake/rooms/{roomId}/kick/{playerId}`
- 路径参数: `roomId`, `playerId`
- 返回:
```json
{ "code": 0, "data": true, "msg": "success" }
```

### 8.5 获取配置
- `GET /game/admin/snake/config`
- 返回:
```json
{
  "code": 0,
  "data": { "boostMultiplier": 2, "foodMaxRatio": 0.25 },
  "msg": "success"
}
```

### 8.6 更新配置
- `PUT /game/admin/snake/config`
- Body:
```json
{ "boostMultiplier": 2, "foodMaxRatio": 0.25 }
```
- 返回:
```json
{
  "code": 0,
  "data": { "boostMultiplier": 2, "foodMaxRatio": 0.25 },
  "msg": "success"
}
```

### 8.7 发放复活卡
- `POST /game/admin/snake/reviveCards/grant`
- Body:
```json
{ "roomId": "endless-1", "playerId": "uid1", "count": 1 }
```
- 返回:
```json
{ "code": 0, "data": 2, "msg": "success" }
```

## 9. 自动进入指定模式房间
- `GET /game/guest/snake/autoJoin`
- Query:
  - `mode`: ENDLESS|BATTLE (默认 ENDLESS)
  - `name`: 昵称 (可选)
  - `playerId`: 玩家 ID (可选, 未登录时需要)
- 返回:
```json
{
  "code": 0,
  "data": {
    "roomId": "endless-1",
    "mode": "ENDLESS",
    "roundNo": 1,
    "status": "RUNNING",
    "players": [
      { "playerId": "uid1", "name": "Alice", "ready": false, "alive": true, "score": 0, "length": 3, "reviveCards": 0 }
    ]
  },
  "msg": "success"
}
```

## 10. 复活与复活卡
### 10.1 复活
- `POST /game/user/snake/revive`
- Body:
```json
{ "roomId": "endless-1", "playerId": "uid1" }
```
- 返回:
```json
{ "code": 0, "data": true, "msg": "success" }
```
- 说明: 复活后保留积分与蛇长度，需登录用户

### 10.2 兑换复活卡
- `POST /game/user/snake/reviveCard/exchange`
- Body:
```json
{ "roomId": "endless-1", "playerId": "uid1", "count": 1 }
```
- 返回:
```json
{ "code": 0, "data": 1, "msg": "success" }
```
- 说明: 需登录用户
