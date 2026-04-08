# 邮件服务

邮件发送能力通过后台模块的模板服务统一提供，可在后台配置邮件模板后直接调用。

## 典型用法

```java
import work.soho.admin.api.service.EmailApiService;

private final EmailApiService emailApiService;

Random random = new Random();
Integer code = random.nextInt(8999) + 1000;

Map<String, Object> model = new HashMap<>();
model.put("code", code);

emailApiService.sendEmail(chatUser.getEmail(), "code", model);
```

## 说明

- 第二个参数通常为模板标识。
- 第三个参数为模板渲染变量。
- 建议模板变量命名与后台模板占位符保持一致。
