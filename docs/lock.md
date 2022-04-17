# 分布式锁

分布式锁暂时使用的是 RedissonClient， 在本项目中进行了简化封装。

## 配置文件配置

配置文件中只需要配置redis地址即可

    redisson:
      nodeAddress: redis://127.0.0.1:6379

## 使用

    //加锁
    LockUtils.getLockClient().getLock("lockName").lock()
    //解锁
    LockUtils.getLockClient().getLock("lockName").unlock()

## 更多信息

更多相信文档信息请参考： http://redisson.org