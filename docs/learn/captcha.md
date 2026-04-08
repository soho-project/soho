# 验证码

本文档说明项目内图形验证码的基本使用方式。

## 创建并输出验证码

```java
CaptchaUtils.createAndSend();
```

## 校验验证码

```java
Boolean success = CaptchaUtils.checking(code);
```

## 清理验证码缓存

```java
CaptchaUtils.dropCaptcha();
```

## 注意事项

- 验证码校验通过后，且业务处理成功后，应及时清理验证码缓存。
- 如果验证码校验通过但未清理缓存，可能会导致重复提交或绕过验证码校验的问题。
