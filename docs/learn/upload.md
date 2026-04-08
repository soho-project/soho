# 上传通用指南

项目的上传能力基于统一配置封装，支持多种底层存储渠道，例如：

- 阿里云 OSS
- 腾讯 COS
- 七牛云
- 本地文件系统
- SMB 共享目录

## 配置示例

```yaml
upload:
  enable: true
  defaultChannel: oss
  oss:
    enable: true
    type: oss
    config:
      endpoint: oss-cn-shenzhen.aliyuncs.com
      accessKeyId: xxxx
      accessKeySecret: xxxx
      bucketName: xxxx
      urlPrefix: https://xxxx.aliyuncs.com/
  oss2:
    enable: true
    type: oss
    config:
      endpoint: oss-cn-shenzhen.aliyuncs.com
      accessKeyId: xxxx
      accessKeySecret: xxxx
      bucketName: xxxx
      urlPrefix: https://xxxx.aliyuncs.com/
  cos:
    enable: true
    type: cos
    config:
      secretId: xxxx
      secretKey: xxxx
      region: ap-guangzhou
      bucketName: soho-admin-demo-1258009624
      urlPrefix: https://xxxx.file.myqcloud.com/
  qiniu:
    enable: true
    type: qiniu
    config:
      accessKey: xxx
      secretKey: xxxx
      bucket: xxxx
      urlPrefix: http://ra86b7o3s.hn-bkt.clouddn.com/
  file:
    enable: true
    type: file
    config:
      baseDir: D:\\data\\
      urlPrefix: http://ra86b7o3s.hn-bkt.clouddn.com/
  smb:
    enable: true
    type: smb
    config:
      hostname: [主机名]
      username: [用户名]
      password: [密码]
      shareName: [共享名]
      domain: DOMAIN
      urlPrefix: http://res.liufang.org.cn/
      pathPrefix: [路径前缀，可为空]
```

## 使用示例

```java
// 上传到默认通道，取决于 upload.defaultChannel
UploadUtils.upload("test/utils.txt", "hello utils");

// 上传到指定通道
UploadUtils.upload("oss", "test/utils.txt", "hello utils");
UploadUtils.upload("oss2", "test/utils.txt", "hello utils");
UploadUtils.upload("cos", "test/utils.txt", "hello utils");
```

## 说明

- `defaultChannel` 用于指定默认上传通道。
- `type` 决定底层上传实现。
- `urlPrefix` 通常用于拼接最终访问地址。
- 如果需要文件元数据管理、秒传与引用计数，建议配合 [uploadFile.md](uploadFile.md) 一起使用。
