# 短信服务

短信模板发送能力由系统后台统一提供，业务模块可通过后台配置模板后直接调用。

## 典型用法

```java
import work.soho.admin.api.service.SmsApiService;

private final SmsApiService smsApiService;

Random random = new Random();
Integer code = random.nextInt(8999) + 1000;

redisTemplate.opsForValue().set("phone:" + chatUser.getPhone(), code);

Map<String, String> map = new HashMap<>();
map.put("code", String.valueOf(code));

smsApiService.sendSms(chatUser.getPhone(), "code", map);
```

## 说明

- 第二个参数通常为短信模板标识。
- 模板变量应与后台模板中的占位符一致。
- 如涉及验证码，建议同时设置过期时间并限制重试次数。
