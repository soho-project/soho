# 单词消消消乐（Word Match Battle）
## 研发落地版 PRD（Engineering PRD）

> 本文档面向：前端 / 后端 / 架构 / 测试  
> 目标：**可直接进入研发排期与拆任务**

---

## 1. 技术总体架构

### 1.1 架构概览
- 客户端：Web / 小程序 / App
- 通信方式：HTTP + WebSocket
- 核心服务：
  - 用户服务
  - 对战服务
  - 词库服务
  - 排位&赛季服务

```
Client
  ├── HTTP（登录 / 配置 / 词库）
  └── WebSocket（实时对战）
        └── Battle Service
              ├── Match Making
              ├── State Sync
              └── Result Settlement
```

---

## 2. 客户端实现规范

### 2.1 技术选型
- 渲染：Canvas / WebGL
- 状态管理：本地状态机
- 本地缓存：IndexedDB / Storage

### 2.2 核心模块
| 模块 | 说明 |
|----|----|
| BoardEngine | 棋盘生成 & 消除 |
| WordEngine | 单词校验 |
| BattleSync | 对战状态同步 |
| SkillEngine | 技能系统 |
| UI Layer | 展示层 |

---

## 3. 服务端模块拆分

### 3.1 用户服务（user-service）
- 登录 / 注册
- 等级 / 经验
- 词汇掌握度

### 3.2 词库服务（word-service）
- 分级词库
- 错词统计
- 动态难度

### 3.3 对战服务（battle-service）
- 房间管理
- 状态同步
- 干扰结算

---

## 4. WebSocket 对战协议设计

### 4.1 连接流程
1. 客户端请求匹配
2. 分配房间 ID
3. 建立 WS 连接
4. 同步初始棋盘

### 4.2 消息结构（统一）

```json
{
  "type": "EVENT_TYPE",
  "roomId": "xxxx",
  "userId": "u123",
  "payload": {}
}
```

### 4.3 关键消息类型
| type | 说明 |
|----|----|
| MATCH_START | 开始 |
| BOARD_SYNC | 棋盘同步 |
| WORD_CLEAR | 消除 |
| SKILL_CAST | 技能 |
| ATTACK | 干扰 |
| GAME_OVER | 结束 |

---

## 5. 匹配与排位算法

### 5.1 匹配规则
- 段位 ±1
- 词汇量区间接近
- 等待时间兜底

### 5.2 ELO 简化模型
```
newScore = oldScore + K * (result - expected)
```

---

## 6. 词库与数据结构设计

### 6.1 单词表（word）
```json
{
  "id": 1,
  "word": "abandon",
  "meaning": "放弃",
  "level": "CET4",
  "freq": 0.82
}
```

### 6.2 错词表（user_word_error）
```json
{
  "userId": "u1",
  "wordId": 1,
  "errorCount": 3,
  "lastErrorTime": 1700000000
}
```

---

## 7. 结算与回放

### 7.1 结算逻辑
- 正确率
- 连击数
- 高级词数量

### 7.2 回放存储
- 关键操作事件流
- 用于复盘 / 申诉

---

## 8. 反作弊与风控

- 消除频率检测
- 词汇正确率异常
- WS 心跳校验

---

## 9. 日志与监控

| 项目 | 指标 |
|----|----|
| 对战 | 延迟 / 掉线 |
| 学习 | 正确率 |
| 系统 | QPS / CPU |

---

## 10. 测试建议

- 单词正确性测试
- 并发对战压测
- 网络抖动测试

---

## 11. 版本拆期建议

### V1.0
- 单人模式
- 1v1 对战

### V1.1
- 排位赛
- 赛季系统

### V1.2
- 4 人混战
- AI 对手

---

## 12. 总结

> 本文档目标：  
> **让前后端看到就能直接开干，不需要二次翻译需求。**
