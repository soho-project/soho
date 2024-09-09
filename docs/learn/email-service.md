邮件发送服务
==========

调用右键模块发送邮件，可在后台直接配置右键模板


Example
--------

    import work.soho.api.admin.service.EmailApiService;

    private final EmailApiService emailApiService;

    Random random = new Random();
    Integer code = random.nextInt(8999) + 1000;
    Map<String, Object> model = new HashMap<>();
    model.put("code", code);
    emailApiService.sendEmail(chatUser.getEmail(), "code", model);
