短信服务
=======

    该短信服务为调用系统模块的模板配置服务发送

Example
-------

    import service.work.soho.admin.api.SmsApiService;

    private final SmsApiService smsApiService;

    Random random = new Random();
    Integer code = random.nextInt(8999) + 1000;
    redisTemplate.opsForValue().set("phone:" + chatUser.getPhone(), code);
    map.put("code", String.valueOf(code));
    smsApiService.sendSms(chatUser.getPhone(), "code", map);
