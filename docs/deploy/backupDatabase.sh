#!/bin/bash

script_dir=$(dirname "$(readlink -f "$0")")
cd $script_dir

# 替换以下内容为你的实际数据库信息和路径
DB_HOST=192.168.0.101
DB_USER=dev
DB_PASSWORD=Dev@123456789
BACKUP_DIR="$script_dir/../databases"

# 从配置文件读取数据库列表
CONFIG_FILE="$script_dir/databases.conf"
if [ -f "$CONFIG_FILE" ]; then
    # 读取配置文件，跳过空行和注释行
    DATABASES=()
    while IFS= read -r line || [[ -n "$line" ]]; do
        # 跳过空行和以#开头的注释行
        if [[ -n "$line" && ! "$line" =~ ^[[:space:]]*# ]]; then
            DATABASES+=("$line")
        fi
    done < "$CONFIG_FILE"

    echo "从配置文件读取到 ${#DATABASES[@]} 个数据库"
else
    echo "配置文件 $CONFIG_FILE 不存在，使用默认数据库列表"
    # 默认数据库列表
    DATABASES=("dev")
fi

# 获取当前日期和时间
CURRENT_DATE=$(date +"%Y_%m_%d_%H_%M_%S")

# 创建备份目录（如果不存在）
mkdir -p "$BACKUP_DIR"

# 备份每个数据库
for DB_NAME in "${DATABASES[@]}"
do
    echo "正在备份数据库: $DB_NAME"

    BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}-${CURRENT_DATE}.sql"

    if mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE; then
        echo "✓ 数据库 $DB_NAME 备份成功"
        echo "  文件: $BACKUP_FILE"
        echo "  大小: $(($(wc -c < "$BACKUP_FILE")/1024)) KB"
    else
        echo "✗ 错误: 数据库 $DB_NAME 备份失败!"
        # 检查数据库是否存在
        echo "  正在检查数据库是否存在..."
        if mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD -e "USE $DB_NAME" 2>/dev/null; then
            echo "  数据库存在，但备份失败，请检查权限或磁盘空间"
        else
            echo "  数据库不存在或无法连接"
        fi
    fi

    echo ""
done

echo "备份完成!"
echo "备份文件保存在: $BACKUP_DIR"