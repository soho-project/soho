短信发送
=======

配置
----

    sms:
      enable: true
      # 配置短信默认通道， 默认通道名为 aliyun
      defaultChannel: aliyun
      channels:
        # 阿里云通道， 通道名为 aliyun
        aliyun:
          enable: true
          type: aliyun
          config:
            endpoint: dysmsapi.aliyuncs.com
            accessKeyId: key id
            accessKeySecret: secret
        # 腾讯短信通道， 通道名为 tencent
        tencent:
          type: tencent
          config:
            secretId: XXXX
            secretKey: YYYYY
            region: ap-guangzhou
            endpoint: sms.tencentcloudapi.com
            sdkAppid: 1400673508

使用
---

    //创建短信消息
    HashMap<String, String> map  = new HashMap<>();
        map.put("code", "2222");
        Message message = new Message();
        message.setSignName("青春无极限")
                .setPhoneNumbers("+8615873164073")
                .setTemplateCode("1392711")
                .setOutId(String.valueOf(IDGeneratorUtils.snowflake().longValue()))
                .setParams(map);

    //默认通道发送短信
    SmsUtils.sendSms(message)
    //指定通道名发送短信
    SmsUtils.sendSms("tencent", message);