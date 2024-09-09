# SOHO项目标准

## URL路径标准

样例可以参考 example模块： /example/admin/exampleCategory/tree; 其中第一层为模块，第二层为项目角色，第三层为控制器/功能, 第四层为方法。

- 模块名称 同一个模块应该保持统一的前缀明明称，如：example
- 项目角色 同一个项目中可以有多个角色，如：admin(管理用户)、user(普通用户)、guest(访客)
- 控制器 模块中的控制器名称，一般同模块功能资源紧密相关，如：exampleCategory
- 方法 控制器中的方法名称，对应具体功能，例如增删查改，如：tree