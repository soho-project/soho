# 项目版本管理


## 递归修改项目模块版本号

    # 将项目的版本号设置为1.0，递归修改所有模块的版本号
    mvn versions:set -DnewVersion=1.0 -DgenerateBackupPoms=false