短信发送
=======

配置
----

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

使用
---

    //message 参考 work.soho.common.data.sms.Message
    SmsUtils.sendSms(message)