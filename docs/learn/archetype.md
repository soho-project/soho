# Maven Archetype 工具

## 1. 创建项目

工具包地址：

- <https://gitee.com/work-soho/soho-archetype-project>

```bash
mvn archetype:generate \
  -DarchetypeGroupId=work.soho \
  -DarchetypeArtifactId=archetype-project \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=work.soho \
  -DartifactId=app
```

## 2. 创建模块

该命令需要先安装 Maven 插件。运行后会创建一个业务模块及其配套子模块，通常包括：

- 业务模块
- 业务 API 模块
- cloud 实现模块
- cloud API 模块
- cloud bridge 模块

可以根据实际需求自行裁剪，通常“删除多余模块”比“后补模块边界”更容易。

工具包地址：

- <https://gitee.com/work-soho/soho-archetype-cloud-module>

```bash
mvn archetype:generate \
  -DarchetypeGroupId=work.soho \
  -DarchetypeArtifactId=archetype-cloud-module \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=work.soho \
  -DartifactId=air
```
