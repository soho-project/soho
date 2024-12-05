#!/bin/bash

script_dir=$(dirname "$(readlink -f "$0")")
cd $script_dir

# 替换以下内容为你的实际数据库信息和路径
DB_HOST=192.168.0.101
DB_USER=dev
DB_PASSWORD=Dev@123456789
DB_NAME=dev
BACKUP_DIR="$script_dir/../databases"

# 获取当前日期和时间
CURRENT_DATE=$(date +"%Y_%m_%d_%H_%M_%S")

# 构建备份文件名
BACKUP_FILE="${BACKUP_DIR}/soho-admin-${CURRENT_DATE}.sql"

# 使用 mysqldump 命令备份指定表的结构
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE

# 数据脱敏
../shell/cleanDb.sh
