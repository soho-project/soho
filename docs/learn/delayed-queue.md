# 延时消息队列

项目内提供了对延时执行与延时事件投递的简化封装。

## 执行型延时任务

```java
DelayedQueueUtils.addExecDelayedMessage(ExecDelayedMessage message);
DelayedQueueUtils.addExecDelayedMessage(Runnable message, long delayMilliseconds);
```

## 事件型延时任务

```java
DelayedQueueUtils.addEventDelayedMessage(Object message, long delayMilliseconds);
```

## 删除延时任务

若需要删除延时任务，初始化消息时必须设置：

- `id`
- `groupName`

其中 `groupName` 默认值为 `default`。

```java
// 按照 id 与 groupName 删除延时任务
DelayedQueueUtils.delete(id, groupName);

// 直接按消息对象删除，前提是消息已设置 id 与 groupName
DelayedQueueUtils.delete(message);
```
