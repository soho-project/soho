# Maven archetype工具

## 1. 创建项目

    //工具包安装地址 https://gitee.com/work-soho/soho-archetype-project
    mvn archetype:generate  \
    -DarchetypeGroupId=work.soho  \
    -DarchetypeArtifactId=archetype-project \
    -DarchetypeVersion=1.0-SNAPSHOT  \
    -DgroupId=work.soho  \
    -DartifactId=app

## 2. 创建模块

该命令需要先安装maven插件；运行后会创建一个模块，四个子模块，分别为： 业务模块，业务api模块，cloud实现模块， cloud  api 模块, cloud bridge模块模块； 可以根据需求自行添加删除（删除总比添加简单多了）；

    //工具包安装地址 https://gitee.com/work-soho/soho-archetype-cloud-module
    mvn archetype:generate  \
    -DarchetypeGroupId=work.soho  \
    -DarchetypeArtifactId=archetype-cloud-module \
    -DarchetypeVersion=1.0-SNAPSHOT  \
    -DgroupId=work.soho  \
    -DartifactId=air