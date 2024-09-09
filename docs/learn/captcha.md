验证码
=====

创建发送验证码

    CaptchaUtils.createAndSend();

验证验证码

    Boolean CaptchaUtils.checking(code)

删除验证码存储

    CaptchaUtils.dropCaptcha();


注意事项
------

验证码验证通过 && 正确处理业务逻辑后请清除验证码信息存储 避免绕过验证码的BUG