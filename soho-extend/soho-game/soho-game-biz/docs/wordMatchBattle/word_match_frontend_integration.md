# 单词消消消乐 前端对接文档（整理版）

> 适用范围：Web / 小程序 / App
> 对接内容：HTTP + WebSocket（LongLink）

## 0. 约定

- Base URL: `http(s)://<host>:<port>`
- WS 地址：`ws://<host>:<port>/ws`
- 认证：`/game/user/word-match/**` 需要登录（Bearer Token / Cookie 等）
- WS 通用消息结构（LongLink）：

```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "xxx",
  "payload": {}
}
```

## 1. HTTP 接口

### 1.1 自动匹配/创建房间

GET `/game/user/word-match/match/auto`

请求参数：
- `mode` (可选): `DUEL|RANKED|FOUR|SOLO|AI`
- `name` (可选): 昵称
- `playerId` (可选): 不传则用登录用户
- `wordLevel` (可选): 词库等级，如 `CET4/CET6/IELTS`

返回示例（成功）：
```json
{
  "code": 2000,
  "msg": "success",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "mode": "DUEL",
    "status": "WAITING",
    "maxPlayers": 2,
    "players": ["1001"],
    "scores": {"1001": 0}
  }
}
```

返回示例（匹配中）：
```json
{
  "code": 5000,
  "msg": "matching"
}
```

### 1.2 获取房间快照

GET `/game/user/word-match/room`

请求参数：
- `roomId` (必填)

返回示例：
```json
{
  "code": 2000,
  "msg": "success",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "mode": "DUEL",
    "status": "RUNNING",
    "maxPlayers": 2,
    "players": ["1001","1002"],
    "scores": {"1001": 10, "1002": 6}
  }
}
```

### 1.3 提交单词

POST `/game/user/word-match/submitWord`

请求体：
```json
{
  "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
  "playerId": "1001",
  "word": "apple",
  "wordLevel": "CET4"
}
```

返回示例（成功）：
```json
{
  "code": 2000,
  "msg": "success",
  "payload": {
    "accepted": true,
    "scoreDelta": 10,
    "combo": 2,
    "message": "accepted",
    "boardDelta": {
      "cleared": [{"x":1,"y":2},{"x":2,"y":2}],
      "filled": [{"x":1,"y":0,"value":"K"},{"x":2,"y":0,"value":"S"}],
      "board": [
        ["A","K","S","D"],
        ["F","G","H","J"]
      ]
    }
  }
}
```

返回示例（失败）：
```json
{
  "code": 2000,
  "msg": "success",
  "payload": {
    "accepted": false,
    "scoreDelta": 0,
    "combo": 0,
    "message": "word not on board",
    "boardDelta": null
  }
}
```

### 1.4 技能/干扰（可选）

POST `/game/user/word-match/skill`
```json
{"roomId":"xxx","playerId":"1001","skillCode":"FREEZE"}
```

POST `/game/user/word-match/attack`
```json
{
  "roomId":"xxx",
  "fromPlayerId":"1001",
  "targetPlayerId":"1002",
  "attackType":"RESET_COMBO",
  "payload":{"resetCombo":true}
}
```

### 1.5 离开房间

POST `/game/user/word-match/leave`

请求参数：
- `roomId`
- `playerId`

返回示例：
```json
{"code":2000,"msg":"success","payload":true}
```

### 1.6 提示单词（HTTP）

GET `/game/user/word-match/hint`

请求参数：
- `roomId` (必填)
- `playerId` (可选，不传则用登录用户)
- `wordLevel` (可选)

返回示例（成功）：
```json
{
  "code": 2000,
  "msg": "success",
  "payload": "APPLE"
}
```

返回示例（无提示）：
```json
{
  "code": 5000,
  "msg": "no hint"
}
```

### 1.7 词库获取（游客）

GET `/game/guest/word-match/words/list?level=CET4`

返回示例：
```json
{
  "code": 2000,
  "msg": "success",
  "payload": [
    {"word":"abandon","meaning":"放弃","level":"CET4","freq":0.82}
  ]
}
```

## 2. WebSocket（LongLink）对接

### 2.1 客户端 -> 服务端

#### a) join（加入/匹配）
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "join",
  "payload": {
    "roomId": null,
    "mode": "DUEL",
    "name": "Alice",
    "playerId": "1001",
    "wordLevel": "CET4"
  }
}
```

#### b) submit/submitWord（提交单词）
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "submit",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "playerId": "1001",
    "word": "apple",
    "wordLevel": "CET4"
  }
}
```

#### c) sync（拉取棋盘）
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "sync",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "playerId": "1001"
  }
}
```

#### d) leave
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "leave",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "playerId": "1001"
  }
}
```

#### e) ping
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "ping",
  "payload": {"clientTime": 1710000000000}
}
```

### 2.2 服务端 -> 客户端

#### ROOM_SYNC
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "ROOM_SYNC",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "mode": "DUEL",
    "status": "WAITING",
    "maxPlayers": 2,
    "players": ["1001"],
    "scores": {"1001":0}
  }
}
```

#### MATCH_START
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "MATCH_START",
  "payload": {
    "roomId": "c1d3c0a7-4f2b-4f15-acde-2ac5c2e0fd5e",
    "status": "RUNNING"
  }
}
```

#### BOARD_SYNC
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "BOARD_SYNC",
  "payload": {
    "playerId": "1001",
    "board": [
      ["A","K","S","D"],
      ["F","G","H","J"]
    ]
  }
}
```

#### WORD_CLEAR
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "WORD_CLEAR",
  "payload": {
    "playerId": "1001",
    "word": "apple",
    "response": {
      "accepted": true,
      "scoreDelta": 10,
      "combo": 2,
      "message": "accepted",
      "boardDelta": {
        "cleared": [{"x":1,"y":2}],
        "filled": [{"x":1,"y":0,"value":"K"}],
        "board": [["A","K","S","D"]]
      }
    }
  }
}
```

#### SCORE_SYNC
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "SCORE_SYNC",
  "payload": {
    "roomId": "xxx",
    "scores": {"1001": 12, "1002": 9}
  }
}
```

#### pong / ERROR
```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "pong",
  "payload": {"clientTime":1710000000000,"serverTime":1710000000012}
}
```

```json
{
  "namespace": "soho-game",
  "topic": "word-match",
  "type": "ERROR",
  "payload": {"message":"room not found"}
}
```

## 3. 备注

- 每个玩家独立棋盘（server 维护）。
- `submitWord` 成功会广播 `WORD_CLEAR` 给房间；失败只回给提交者。
- 如需共享棋盘或不同消除规则（对角线、最小字长等），需要后端调整。
