长链接模块
=========

简介
----
基于 Netty 的 WebSocket 长连接模块，支持认证、消息事件分发与服务端主动推送。

连接与配置
----------
默认连接地址:

```
http://IP:8080/ws
```

可配置项:

- `longlink.port`: WebSocket 端口 (默认 8080)
- `longlink.path`: WebSocket 路径 (默认 /ws)

认证方式
--------
支持在握手阶段或首帧进行认证。可用以下方式携带 token:

- Header: `Authorization: Bearer <token>` 或 `X-Token`/`X-Access-Token`
- Cookie: `token`/`access_token`
- Query: `?token=...` 或 `?access_token=...`
- 首帧: 纯文本 token 或 JSON (`{"token":"..."}` / `{"access_token":"..."}`)

首帧认证示例:

```
// 发送 token
eyJhbGciOiJIUzUxMiJ9.eyJ1aWQiOjEsInVuYW1lIjoiMTU4NzMxNjQwNzMiLCJleHAiOjE2OTA0ODk0NTgsImlhdCI6MTY4OTE3NTQ1OH0.eAoMkpnIi74atazzi3Ag4Qrw7yFKBekNbg_h0VkV7sgFG9iHuOi7XRV24-j7unGc_J51YxifC_wdgIocjuw7Pg

// 成功
+OK
```

消息接收
--------
客户端消息以事件形式发布，监听 `MessageEvent` 即可接收:

```
work.soho.longlink.api.event.MessageEvent
```

服务端推送
----------
使用 `Sender` Bean 发送消息到用户或连接:

```
void sendToUid(String uid, String msg);
void sendToConnectId(String connectId, String msg);
void sendToAllUid(String msg);
void sendToAllConnect(String msg);
void bindUid(String connectId, String uid);
```

监控接口
--------
仅返回当前服务实例内的连接信息:

```
GET /guest/link/info/list   // 当前连接列表
GET /guest/link/info/count  // 当前连接数
```
