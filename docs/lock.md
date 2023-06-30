# 分布式锁

分布式锁暂时使用的是 RedissonClient， 在本项目中进行了简化封装。

## 配置文件配置

配置文件中只需要配置redis地址即可

    redisson:
      nodeAddress: redis://127.0.0.1:6379

## 使用

### 工具类方式

    //加锁
    LockUtils.getLockClient().getLock("lockName").lock()
    //解锁
    LockUtils.getLockClient().getLock("lockName").unlock()

### AOP注解方式

    在Bean方法上面添加 @Lock 注解该方法即拥有排他锁； 如果其他线程持有锁，该线程会阻塞等待锁释放； 故而考虑到系统吞吐能力应该尽可能小粒度加锁.
    锁名称支持 SPEL 表达式；

        @Lock("'pre-key-' + #name")
        public void hello(String name) {
            System.out.println("hello............." + name);
        }

## 更多信息

更多相信文档信息请参考： http://redisson.org
