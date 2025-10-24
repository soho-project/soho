# 数据库组件

## 模块分库

最佳实践：

        @Around("execution(* work.soho.[模块包名].biz.service..*+.*(..)) && execution(* com.baomidou.mybatisplus.extension.service.IService+.*(..))")

## TODO 事务


## 关联删除

    // 删除发送通知； 可以直接使用注解
    @PublishBatchDeleteNotify


    // 在方法上添加注解可以接收删除通知
    @OnBeforeBatchDelete