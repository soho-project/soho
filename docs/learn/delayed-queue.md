延时消息队列
==========

- Exec延时队列

      DelayedQueueUtils.addExecDelayedMessage(ExecDelayedMessage message)
      DelayedQueueUtils.addExecDelayedMessage(Runnable message, long delayMilliseconds)

- 延时Event

      DelayedQueueUtils.addEventDelayedMessage(Object message, long delayMilliseconds)

- 删除延时队列

要删除延时队列，在初始化任务 Message 的时候必须设置消息 id, 以及groupName （分组名默认为default）

    // 按照 id, groupName 删除延时队列
    DelayedQueueUtils.delete(id, groupName)
    
    // 删除指定的延时队列 （前提是设置了  id, groupName）
    DelayedQueueUtils.delete(message)