鉴权
====

鉴权接口以及工具类在 soho-common-security 定义。 如果是后台相关配置以及实现已经实现， 其他后台模块只需要映入该模块即可。

其他相关需要鉴权的系统模块需要自己去实现， 建议引入该模块自行定义鉴权， 以及鉴权用户获取接口实现。



控制器中获取认证用户
=================

    @AuthenticationPrincipal SohoUserDetails sohoUserDetails


测试中鉴权
=========

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                //注意这里，开启测试 security
                .apply(springSecurity())
                .build();
    }

    @SohoWithUser(id = 6, username = "197489090675871745",  role = "chat")
