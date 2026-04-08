# 短信发送

本文档说明短信通道的基础配置方式与发送示例。

## 配置示例

```yaml
sms:
  enable: true
  defaultChannel: aliyun
  channels:
    aliyun:
      enable: true
      type: aliyun
      config:
        endpoint: dysmsapi.aliyuncs.com
        accessKeyId: key id
        accessKeySecret: secret
    tencent:
      type: tencent
      config:
        secretId: XXXX
        secretKey: YYYYY
        region: ap-guangzhou
        endpoint: sms.tencentcloudapi.com
        sdkAppid: 1400673508
```

## 使用示例

```java
HashMap<String, String> map = new HashMap<>();
map.put("code", "2222");

Message message = new Message();
message.setSignName("青春无极限")
        .setPhoneNumbers("+8615873164073")
        .setTemplateCode("1392711")
        .setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()))
        .setParams(map);

// 使用默认通道发送
SmsUtils.sendSms(message);

// 指定通道发送
SmsUtils.sendSms("tencent", message);
```

## 说明

- `defaultChannel` 用于指定默认短信通道。
- 不同厂商的 `config` 字段会略有不同，应按对应通道实现填写。
- `outId` 建议使用业务唯一 ID，便于排查与追踪。
